package dev.alta.essentials.utils.gui

import dev.alta.essentials.utils.gui.manager.GuiManager
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Gui(
    internal val inventory: Inventory,
    private val buttons: Map<Int, GuiButton>,
    private val allowItemMovement: Boolean = false
) {
    fun open(player: Player) {
        player.openInventory(inventory)
        GuiManager.registerGui(player, this)
    }
    
    fun handleClick(event: InventoryClickEvent) {
        if (!allowItemMovement) {
            event.isCancelled = true
        }
        buttons[event.slot]?.onClick?.invoke(event)
    }
    
    fun close(player: Player) {
        GuiManager.unregisterGui(player)
    }
}

data class GuiButton(
    val item: ItemStack,
    val onClick: (InventoryClickEvent) -> Unit
) 