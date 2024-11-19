package dev.alta.essentials.listeners

import dev.alta.essentials.annotations.register.AutoRegister
import dev.alta.essentials.annotations.listener.Listener
import dev.alta.essentials.nametag.NametagManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener as BukkitListener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Listener
class NametagListener : BukkitListener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        NametagManager.updatePlayerNametag(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        NametagManager.removePlayerNametag(event.player)
    }
}