package dev.alta.essentials.database.mongo.connection

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import dev.alta.essentials.database.mongo.Mongo
import org.bson.Document
import org.bukkit.plugin.Plugin
import java.util.concurrent.ConcurrentHashMap

class MongoConnection(
    private val plugin: Plugin,
    private val config: Mongo
) : AutoCloseable {
    private val client: MongoClient
    private val database: MongoDatabase
    private val collections = ConcurrentHashMap<String, MongoCollection<Document>>()

    init {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(config.connectionString))
            .apply {
                if (config.username != null && config.password != null) {
                    credential(
                        MongoCredential.createCredential(
                            config.username,
                            config.authDatabase ?: "admin",
                            config.password.toCharArray()
                        )
                    )
                }
            }
            .build()

        client = MongoClients.create(settings)
        database = client.getDatabase(config.database)

        // Initialize configured collections
        config.collections.forEach { (name, collectionName) ->
            collections[name] = database.getCollection(collectionName)
        }
    }

    fun getCollection(name: String): MongoCollection<Document> {
        return collections.computeIfAbsent(name) {
            database.getCollection(name)
        }
    }

    fun getDatabase(): MongoDatabase = database

    override fun close() {
        client.close()
    }
}