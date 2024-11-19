package dev.alta.essentials.reflection

import co.aikar.commands.BaseCommand
import dev.alta.essentials.annotations.register.AutoRegister
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import co.aikar.commands.PaperCommandManager

class CommandRegistrar(private val plugin: JavaPlugin) {
    private val commandManager = PaperCommandManager(plugin).apply {
        enableUnstableAPI("help")
    }

    fun registerCommands() {
        val basePackage = plugin.javaClass.packageName
        
        val reflections = Reflections(
            ConfigurationBuilder()
                .forPackage(basePackage, plugin.javaClass.classLoader)
                .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
        )

        reflections.getTypesAnnotatedWith(AutoRegister::class.java)
            .filter { it.packageName.startsWith(basePackage) }
            .forEach { clazz ->
                try {
                    val instance = clazz.getDeclaredConstructor().newInstance()
                    
                    if (instance is BaseCommand) {
                        commandManager.registerCommand(instance)
                        plugin.logger.info("Registered command: ${clazz.simpleName}")
                    }
                } catch (e: Exception) {
                    plugin.logger.warning("Failed to register command ${clazz.simpleName}: ${e.message}")
                }
            }
    }
} 