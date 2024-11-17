package dev.alta.essentials.listeners

import dev.alta.essentials.utils.annotations.register.AutoRegister
import dev.alta.essentials.utils.nametag.NametagManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@AutoRegister
class NametagListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        NametagManager.updatePlayerNametag(event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        NametagManager.removePlayerNametag(event.player)
    }
}