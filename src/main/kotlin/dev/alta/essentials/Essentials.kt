package dev.alta.essentials

import co.aikar.commands.PaperCommandManager
import co.aikar.commands.CommandCompletions
import co.aikar.commands.BukkitCommandCompletionContext
import dev.alta.essentials.utils.annotations.AnnotationUtils
import dev.alta.essentials.utils.config.ConfigManager
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.luckperms.api.LuckPerms
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import dev.alta.essentials.ranks.manager.RankManager
import dev.alta.essentials.utils.nametag.NametagManager
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.PermissionNode
import org.bukkit.permissions.Permission
import org.bukkit.Bukkit

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
        
        // Initialize NametagManager
        NametagManager
        
        // Rank completions
        commandManager.commandCompletions.registerAsyncCompletion("ranks") { _ ->
            luckPerms.groupManager.loadedGroups.map { it.name }
        }
        
        // Permission completions
        commandManager.commandCompletions.registerAsyncCompletion("permissions") { _ ->
            Bukkit.getPluginManager().permissions
                .map { it.name }
                .sorted()
        }
        
        // Rank-specific permission completions
        commandManager.commandCompletions.registerAsyncCompletion("rankperms") { context: BukkitCommandCompletionContext ->
            val input = context.input
            val args = input.split(" ")
            
            if (args.size >= 2) {
                val rankName = args[1]
                val group = luckPerms.groupManager.getGroup(rankName) ?: return@registerAsyncCompletion emptyList()
                
                group.nodes
                    .filterIsInstance<PermissionNode>()
                    .filter { it.value }
                    .map { it.permission }
                    .sorted()
            } else {
                emptyList()
            }
        }
        
        AnnotationUtils.registerAll(this)
    }

    override fun disable() {
        configManager.saveAllConfigs(async = false)
        _mongoClient?.close()
        _mongoClient = null
    }

    private fun setupMongo() {
        val connectionString = configManager.getConfig().getString("mongodb.connection-string") 
            ?: "mongodb://localhost:27017"
        _mongoClient = MongoClients.create(connectionString)
    }
} 