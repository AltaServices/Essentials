package dev.alta.essentials

import dev.alta.essentials.async.Async
import me.lucko.helper.plugin.ExtendedJavaPlugin
import dev.alta.essentials.reflection.ReflectionManager
import dev.alta.essentials.nametag.NametagManager
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
        
        // Initialize reflection manager and register everything first
        val reflectionManager = ReflectionManager(this)
        reflectionManager.registerAll()
        
        // Initialize NametagManager after listeners are registered
        NametagManager.initialize(this)
    }

    override fun disable() {
        Async.unregisterPlugin(this)
    }
}