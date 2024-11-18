package dev.alta.essentials.utils.database

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import dev.alta.essentials.Essentials
import dev.alta.essentials.utils.async.AsyncUtils
import org.bson.Document
import java.util.UUID
import java.util.concurrent.CompletableFuture

object DatabaseUtils {
    private val mongoClient = Essentials.mongoClient

    fun getDatabase(name: String): MongoDatabase {
        return mongoClient.getDatabase(name)
    }

    fun getCollection(database: String, collection: String): MongoCollection<Document> {
        return getDatabase(database).getCollection(collection)
    }

    fun getCollection(collection: String): MongoCollection<Document> {
        return getDatabase(
            Essentials.configManager.getConfig().getString("mongodb.database") ?: "Essentials"
        ).getCollection(collection)
    }

    fun getPlayerData(uuid: UUID): CompletableFuture<Document?> {
        return AsyncUtils.asyncCallback {
            getCollection("players")
                .find(Filters.eq("uuid", uuid.toString()))
                .first()
        }
    }

    fun updatePlayerData(uuid: UUID, data: Document): CompletableFuture<Void> {
        return AsyncUtils.asyncCallback {
            getCollection("players").updateOne(
                Filters.eq("uuid", uuid.toString()),
                Document("\$set", data),
                UpdateOptions().upsert(true)
            )
        }.thenAccept { }
    }

    fun deletePlayerData(uuid: UUID): CompletableFuture<Void> {
        return AsyncUtils.asyncCallback {
            getCollection("players").deleteOne(
                Filters.eq("uuid", uuid.toString())
            )
        }.thenAccept { }
    }

    fun createIndex(collection: String, field: String, unique: Boolean = false) {
        getCollection(collection).createIndex(
            Indexes.ascending(field),
            IndexOptions().unique(unique)
        )
    }
} 