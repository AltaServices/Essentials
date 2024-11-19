package dev.alta.essentials.database.mongo.manager

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.UpdateOptions
import dev.alta.essentials.async.Async
import dev.alta.essentials.database.mongo.Mongo
import dev.alta.essentials.database.mongo.connection.MongoConnection
import org.bson.Document
import org.bukkit.plugin.Plugin
import java.util.concurrent.CompletableFuture

class MongoManager(
    private val plugin: Plugin,
    private val connection: MongoConnection
) {
    companion object {
        private val connections = mutableMapOf<String, MongoConnection>()

        fun createConnection(plugin: Plugin, config: Mongo): MongoConnection {
            return connections.computeIfAbsent(plugin.name) {
                MongoConnection(plugin, config)
            }
        }

        fun getConnection(plugin: Plugin): MongoConnection? {
            return connections[plugin.name]
        }

        fun closeConnection(plugin: Plugin) {
            connections.remove(plugin.name)?.close()
        }
    }

    fun <T> asyncOperation(block: () -> T): CompletableFuture<T> {
        return Async.asyncCallback(block)
    }

    fun createIndex(collection: String, field: String, unique: Boolean = false) {
        connection.getCollection(collection).createIndex(
            Indexes.ascending(field),
            IndexOptions().unique(unique)
        )
    }

    fun findOne(
        collection: String,
        filter: Document
    ): CompletableFuture<Document?> = asyncOperation {
        connection.getCollection(collection)
            .find(filter)
            .first()
    }

    fun updateOne(
        collection: String,
        filter: Document,
        update: Document,
        upsert: Boolean = false
    ): CompletableFuture<Void> = asyncOperation {
        connection.getCollection(collection)
            .updateOne(filter, update, UpdateOptions().upsert(upsert))
    }.thenAccept { }

    fun deleteOne(
        collection: String,
        filter: Document
    ): CompletableFuture<Void> = asyncOperation {
        connection.getCollection(collection).deleteOne(filter)
    }.thenAccept { }
}