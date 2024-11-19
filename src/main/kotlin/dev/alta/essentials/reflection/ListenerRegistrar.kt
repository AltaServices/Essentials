package dev.alta.essentials.reflection

import dev.alta.essentials.annotations.listener.Listener
import org.bukkit.event.Listener as BukkitListener
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder

class ListenerRegistrar(private val plugin: JavaPlugin) {
    fun registerListeners() {
        val basePackage = plugin.javaClass.packageName
        
        val reflections = Reflections(
            ConfigurationBuilder()
                .forPackage(basePackage, plugin.javaClass.classLoader)
                .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
        )

        reflections.getTypesAnnotatedWith(Listener::class.java)
            .filter { it.packageName.startsWith(basePackage) }
            .forEach { clazz ->
                try {
                    val instance = clazz.getDeclaredConstructor().newInstance()
                    
                    if (instance is BukkitListener) {
                        plugin.server.pluginManager.registerEvents(instance, plugin)
                        plugin.logger.info("Registered listener: ${clazz.simpleName}")
                    }
                } catch (e: Exception) {
                    plugin.logger.warning("Failed to register listener ${clazz.simpleName}: ${e.message}")
                }
            }
    }
} 