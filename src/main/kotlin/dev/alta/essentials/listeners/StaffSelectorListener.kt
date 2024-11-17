package dev.alta.essentials.listeners

import dev.alta.essentials.staff.gui.StaffGroupSelector
import dev.alta.essentials.utils.annotations.register.AutoRegister
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

@AutoRegister
class StaffSelectorListener : Listener {
    
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.view.title != "Staff Group Selector") return
        event.isCancelled = true
    }
    
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.view.title != "Staff Group Selector") return
    }
} 