package dev.alta.essentials.utils.annotations

import co.aikar.commands.BaseCommand
import dev.alta.essentials.Essentials
import dev.alta.essentials.utils.annotations.register.AutoRegister
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.bukkit.event.Listener

object AnnotationUtils {
    fun registerAll(plugin: Essentials) {
        val reflections = Reflections(
            "dev.alta.essentials",
            Scanners.TypesAnnotated
        )
        
        val annotatedClasses = reflections.getTypesAnnotatedWith(AutoRegister::class.java)
        
        for (clazz in annotatedClasses) {
            val instance = clazz.getDeclaredConstructor().newInstance()
            
            when (instance) {
                is Listener -> plugin.server.pluginManager.registerEvents(instance, plugin)
                is BaseCommand -> Essentials.commandManager.registerCommand(instance)
            }
        }
    }
} 