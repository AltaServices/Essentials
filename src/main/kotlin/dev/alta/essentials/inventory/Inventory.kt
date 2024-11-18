package dev.alta.essentials.inventory

import dev.alta.essentials.adventure.MiniMessage.toComponent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.Bukkit

object Inventory {
    fun PlayerInventory.hasSpace(): Boolean {
        return storageContents.any { it == null }
    }

    fun PlayerInventory.firstEmptySlot(): Int {
        return storageContents.indexOfFirst { it == null }
    }

    fun Player.giveOrDrop(item: ItemStack) {
        val remaining = inventory.addItem(item)
        remaining.values.forEach { world.dropItemNaturally(location, it) }
    }

    fun Player.clearInventoryExceptArmor() {
        val armor = inventory.armorContents
        inventory.clear()
        inventory.armorContents = armor
    }

    fun Inventory.lockAllItems() {
        contents.forEachIndexed { index, item ->
            if (item != null) {
                setItem(index, item.clone().apply {
                    itemMeta = itemMeta?.apply {
                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                        isUnbreakable = true
                    }
                })
            }
        }
    }

    fun Inventory.preventItemMovement(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    fun Player.openLockedGui(inventory: Inventory, title: String? = null) {
        val gui = if (title != null) {
            Bukkit.createInventory(null, inventory.size, title.toComponent())
        } else {
            Bukkit.createInventory(null, inventory.size)
        }
        
        gui.contents = inventory.contents
        gui.lockAllItems()
        openInventory(gui)
    }

    fun isGuiClick(event: InventoryClickEvent): Boolean {
        return event.clickedInventory?.holder == null || 
               event.clickedInventory?.holder !is Player
    }
} 