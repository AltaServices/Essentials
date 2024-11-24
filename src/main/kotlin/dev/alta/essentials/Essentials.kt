package dev.alta.essentials

import dev.alta.essentials.async.Async
import dev.alta.essentials.player.PlayerManager
import me.lucko.helper.plugin.ExtendedJavaPlugin
import dev.alta.essentials.reflection.ReflectionManager
import dev.alta.essentials.nametag.NametagManager
import net.luckperms.api.LuckPerms
import dev.alta.essentials.database.mongo.manager.MongoManager
import dev.alta.essentials.database.mongo.Mongo
import dev.alta.essentials.config.manager.ConfigManager

class Essentials : ExtendedJavaPlugin() {
    companion object {
        lateinit var instance: Essentials
            private set
        lateinit var luckPerms: LuckPerms
            private set
    }

    lateinit var mongoManager: MongoManager
    lateinit var configManager: ConfigManager
    
    override fun enable() {
        instance = this
        
        // Initialize ConfigManager first
        configManager = ConfigManager(this)
        saveDefaultConfig()
        reloadConfig()
        
        // Initialize LuckPerms
        luckPerms = server.servicesManager.getRegistration(LuckPerms::class.java)?.provider
            ?: throw IllegalStateException("LuckPerms not found!")
        
        // Register with Async utility
        Async.registerPlugin(this)
        
        // Setup MongoDB
        setupMongo()
        
        // Initialize reflection manager and register everything first
        val reflectionManager = ReflectionManager(this)
        reflectionManager.registerAll()
        
        // Initialize PlayerManager with MongoDB
        PlayerManager.initialize(mongoManager)
        PlayerManager.loadFromDatabase()
        
        // Initialize NametagManager after listeners are registered
        NametagManager.initialize(this)
    }

    private fun setupMongo() {
        val config = Mongo(
            connectionString = configManager.getString("mongodb.connection-string") 
                ?: "mongodb://localhost:27017",
            database = configManager.getString("mongodb.database") 
                ?: "essentials",
            collections = mapOf(
                "players" to "players"
            ),
            username = if (configManager.getBoolean("mongodb.auth.enabled")) 
                configManager.getString("mongodb.auth.username") else null,
            password = if (configManager.getBoolean("mongodb.auth.enabled"))
                configManager.getString("mongodb.auth.password") else null,
            authDatabase = if (configManager.getBoolean("mongodb.auth.enabled"))
                configManager.getString("mongodb.auth.database") else null
        )
        
        mongoManager = MongoManager(this, MongoManager.createConnection(this, config))
    }

    override fun disable() {
        MongoManager.closeConnection(this)
        Async.unregisterPlugin(this)
    }
}