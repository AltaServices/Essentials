package dev.alta.essentials.gui.manager

import dev.triumphteam.gui.guis.Gui
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object GuiManager {
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

    fun getGui(player: Player): Gui? {
        return openGuis[player.uniqueId]
    }
} 