package dev.alta.essentials

import co.aikar.commands.PaperCommandManager
import dev.alta.essentials.utils.annotations.AnnotationUtils
import dev.alta.essentials.utils.config.ConfigManager
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.luckperms.api.LuckPerms
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients

class Essentials : ExtendedJavaPlugin() {
    
    companion object {
        lateinit var instance: Essentials
            private set
        lateinit var luckPerms: LuckPerms
            private set
        private var _mongoClient: MongoClient? = null
        val mongoClient: MongoClient get() = _mongoClient 
            ?: throw IllegalStateException("MongoDB client not initialized!")
        lateinit var commandManager: PaperCommandManager
            private set
        lateinit var configManager: ConfigManager
            private set
    }

    override fun enable() {
        instance = this
        configManager = ConfigManager(this)
        luckPerms = server.servicesManager.getRegistration(LuckPerms::class.java)?.provider
            ?: throw IllegalStateException("LuckPerms not found!")
        setupMongo()
        commandManager = PaperCommandManager(this)
        
        // Rank completions
        commandManager.commandCompletions.registerAsyncCompletion("ranks") { _ ->
            luckPerms.groupManager.loadedGroups.map { it.name }
        }
        
        AnnotationUtils.registerAll(this)
    }

    override fun disable() {
        configManager.saveAllConfigs()
        _mongoClient?.close()
        _mongoClient = null
    }

    private fun setupMongo() {
        val connectionString = configManager.getConfig().getString("mongodb.connection-string") 
            ?: "mongodb://localhost:27017"
        _mongoClient = MongoClients.create(connectionString)
    }
} 