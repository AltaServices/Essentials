package dev.alta.essentials.player

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import dev.alta.essentials.Essentials
import dev.alta.essentials.async.Async
import dev.alta.essentials.database.mongo.manager.MongoManager
import org.bson.Document
import java.util.concurrent.CompletableFuture
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import java.util.regex.Pattern

object PlayerManager {
    private val playerCache = ConcurrentHashMap<UUID, PlayerData>()
    private lateinit var mongoManager: MongoManager
    private const val COLLECTION = "players"
    
    data class PlayerData(
        val uuid: UUID,
        val username: String,
        var lastSeen: Long,
        val firstJoin: Long,
        var online: Boolean
    ) {
        fun toDocument(): Document = Document()
            .append("uuid", uuid.toString())
            .append("username", username)
            .append("lastSeen", lastSeen)
            .append("firstJoin", firstJoin)
            .append("online", online)
            
        companion object {
            fun fromDocument(doc: Document): PlayerData = PlayerData(
                uuid = UUID.fromString(doc.getString("uuid")),
                username = doc.getString("username"),
                lastSeen = doc.getLong("lastSeen"),
                firstJoin = doc.getLong("firstJoin"),
                online = doc.getBoolean("online", false)
            )
        }
    }
    
    fun initialize(mongoManager: MongoManager) {
        this.mongoManager = mongoManager
        playerCache.clear()
        
        // Create indexes
        mongoManager.createIndex(COLLECTION, "uuid", true)
        mongoManager.createIndex(COLLECTION, "username")
        mongoManager.createIndex(COLLECTION, "online")
    }
    
    fun updatePlayer(player: Player): CompletableFuture<Void> {
        val playerData = PlayerData(
            uuid = player.uniqueId,
            username = player.name,
            lastSeen = System.currentTimeMillis(),
            firstJoin = player.firstPlayed,
            online = true
        )
        
        playerCache[player.uniqueId] = playerData
        
        return mongoManager.asyncOperation {
            mongoManager.getCollection(COLLECTION)
                .replaceOne(
                    Filters.eq("uuid", player.uniqueId.toString()),
                    playerData.toDocument(),
                    ReplaceOptions().upsert(true)
                )
        }.thenAccept { }
    }
    
    fun setOffline(uuid: UUID): CompletableFuture<Void> {
        playerCache[uuid]?.apply {
            online = false
            lastSeen = System.currentTimeMillis()
        }
        
        return mongoManager.asyncOperation {
            mongoManager.getCollection(COLLECTION)
                .updateOne(
                    Filters.eq("uuid", uuid.toString()),
                    Updates.combine(
                        Updates.set("online", false),
                        Updates.set("lastSeen", System.currentTimeMillis())
                    )
                )
        }.thenAccept { }
    }
    
    fun getPlayerData(uuid: UUID): PlayerData? = playerCache[uuid]
    
    fun getAllPlayers(): List<PlayerData> = playerCache.values.toList()
    
    fun searchPlayers(query: String): List<PlayerData> {
        val lowerQuery = query.lowercase()
        return playerCache.values.filter { 
            it.username.lowercase().contains(lowerQuery)
        }
    }

    fun loadFromDatabase(): CompletableFuture<Void> {
        return mongoManager.asyncOperation {
            mongoManager.getCollection(COLLECTION)
                .find()
                .map { PlayerData.fromDocument(it) }
                .forEach { playerData ->
                    playerCache[playerData.uuid] = playerData
                }
        }.thenAccept { }
    }

    fun getPlayerDataByUsername(username: String): PlayerData? {
        // First check cache
        return playerCache.values.find { it.username.equals(username, ignoreCase = true) }
            ?: run {
                // If not in cache, check database
                val doc = mongoManager.getCollection(COLLECTION)
                    .find(Filters.regex("username", "^${Pattern.quote(username)}$", "i"))
                    .first()
                
                doc?.let { PlayerData.fromDocument(it) }
            }
    }
} 