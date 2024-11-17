package dev.alta.essentials.utils.gui.manager

import dev.alta.essentials.utils.annotations.register.AutoRegister
import dev.alta.essentials.utils.gui.Gui
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@AutoRegister
class GuiManager : Listener {
    companion object {
        private val openGuis = ConcurrentHashMap<UUID, Gui>()

        fun registerGui(player: Player, gui: Gui) {
            openGuis[player.uniqueId] = gui
        }

        fun unregisterGui(player: Player) {
            openGuis.remove(player.uniqueId)
        }

        fun updateGui(player: Player, gui: Gui) {
            openGuis[player.uniqueId] = gui
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        openGuis[player.uniqueId]?.handleClick(event)
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val player = event.whoClicked as? Player ?: return
        if (openGuis.containsKey(player.uniqueId)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        openGuis[player.uniqueId]?.close(player)
    }
}