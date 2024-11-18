//package dev.alta.essentials.annotations
//
//import co.aikar.commands.BaseCommand
//import co.aikar.commands.PaperCommandManager
//import org.bukkit.plugin.Plugin
//import dev.alta.essentials.annotations.register.AutoRegister
//import org.reflections.Reflections
//import org.reflections.scanners.Scanners
//import org.bukkit.event.Listener
//
//object AnnotationUtils {
//    fun registerAll(plugin: Plugin, packageName: String, commandManager: PaperCommandManager) {
//        val reflections = Reflections(
//            packageName,
//            Scanners.TypesAnnotated
//        )
//
//        val annotatedClasses = reflections.getTypesAnnotatedWith(AutoRegister::class.java)
//
//        for (clazz in annotatedClasses) {
//            val instance = clazz.getDeclaredConstructor().newInstance()
//
//            when (instance) {
//                is Listener -> plugin.server.pluginManager.registerEvents(instance, plugin)
//                is BaseCommand -> commandManager.registerCommand(instance)
//            }
//        }
//    }
//}