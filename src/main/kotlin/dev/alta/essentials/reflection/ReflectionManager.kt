package dev.alta.essentials.reflection

import dev.alta.essentials.annotations.register.AutoRegister
import dev.alta.essentials.annotations.listener.Listener
import org.bukkit.event.Listener as BukkitListener
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

class ReflectionManager(private val plugin: JavaPlugin) {
    fun registerAll() {
        val reflections = Reflections(
            ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClassLoader(plugin.javaClass.classLoader))
                .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
        )

        // Register classes with @AutoRegister annotation
        reflections.getTypesAnnotatedWith(AutoRegister::class.java).forEach { clazz ->
            try {
                val instance = clazz.getDeclaredConstructor().newInstance()
                
                // Register Bukkit listeners
                if (instance is BukkitListener) {
                    plugin.server.pluginManager.registerEvents(instance, plugin)
                    plugin.logger.info("Registered listener: ${clazz.simpleName}")
                }
                
                // Add more registration types here as needed
            } catch (e: Exception) {
                plugin.logger.warning("Failed to register ${clazz.simpleName}: ${e.message}")
            }
        }

        // Register classes with @Listener annotation
        reflections.getTypesAnnotatedWith(Listener::class.java).forEach { clazz ->
            try {
                val instance = clazz.getDeclaredConstructor().newInstance()
                
                if (instance is BukkitListener) {
                    plugin.server.pluginManager.registerEvents(instance, plugin)
                    plugin.logger.info("Registered listener: ${clazz.simpleName}")
                } else {
                    plugin.logger.warning("Class ${clazz.simpleName} has @Listener annotation but doesn't implement Bukkit Listener")
                }
            } catch (e: Exception) {
                plugin.logger.warning("Failed to register listener ${clazz.simpleName}: ${e.message}")
            }
        }
    }
} 