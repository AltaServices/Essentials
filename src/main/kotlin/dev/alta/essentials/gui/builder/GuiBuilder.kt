package dev.alta.essentials.gui.builder

import dev.alta.essentials.adventure.MiniMessage.toComponent
import dev.alta.essentials.gui.Gui
import dev.alta.essentials.gui.GuiButton
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class GuiBuilder(
    private val rows: Int,
    private val title: String,
    private val allowItemMovement: Boolean = false
) {
    private val inventory: Inventory = Bukkit.createInventory(null, rows * 9, title.toComponent())
    private val buttons = mutableMapOf<Int, GuiButton>()

    fun setItem(slot: Int, item: ItemStack, onClick: ((InventoryClickEvent) -> Unit)? = null): GuiBuilder {
        inventory.setItem(slot, item)
        onClick?.let { buttons[slot] = GuiButton(item, it) }
        return this
    }

    fun fillBorder(item: ItemStack): GuiBuilder {
        for (i in 0 until rows * 9) {
            if (i < 9 || i >= (rows - 1) * 9 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, item)
            }
        }
        return this
    }

    fun fill(item: ItemStack, vararg slots: Int): GuiBuilder {
        slots.forEach { inventory.setItem(it, item) }
        return this
    }

    fun fillEmpty(item: ItemStack): GuiBuilder {
        for (i in 0 until inventory.size) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, item)
            }
        }
        return this
    }

    fun build(): Gui {
        return Gui(inventory, buttons, allowItemMovement)
    }
}