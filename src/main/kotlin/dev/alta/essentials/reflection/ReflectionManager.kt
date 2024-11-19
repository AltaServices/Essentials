package dev.alta.essentials.reflection

import org.bukkit.plugin.java.JavaPlugin

class ReflectionManager(private val plugin: JavaPlugin) {
    private val commandRegistrar = CommandRegistrar(plugin)
    private val listenerRegistrar = ListenerRegistrar(plugin)

    fun registerAll() {
        commandRegistrar.registerCommands()
        listenerRegistrar.registerListeners()
    }
} 