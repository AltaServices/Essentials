package dev.alta.essentials

import me.lucko.helper.plugin.ExtendedJavaPlugin
import dev.alta.essentials.reflection.ReflectionManager
import net.luckperms.api.LuckPerms

class Essentials : ExtendedJavaPlugin() {
    companion object {
        lateinit var instance: Essentials
            private set
        lateinit var luckPerms: LuckPerms
            private set
    }

    override fun enable() {
        instance = this
        
        // Initialize LuckPerms
        luckPerms = server.servicesManager.getRegistration(LuckPerms::class.java)?.provider
            ?: throw IllegalStateException("LuckPerms not found!")
        
        // Register with Async utility
        Async.registerPlugin(this)
        
        // Initialize reflection manager and register everything
        val reflectionManager = ReflectionManager(this)
        reflectionManager.registerAll()
    }

    override fun disable() {
        Async.unregisterPlugin(this)
    }
}