package dev.alta.essentials.listeners

import dev.alta.essentials.annotations.listener.Listener
import dev.alta.essentials.player.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.Listener as BukkitListener

@Listener
class PlayerTrackingListener : BukkitListener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PlayerManager.updatePlayer(event.player)
    }
    
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        PlayerManager.setOffline(event.player.uniqueId)
    }
} 