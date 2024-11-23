package dev.alta.essentials.gui

import dev.alta.essentials.gui.manager.GuiManager
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.concurrent.CompletableFuture

abstract class StatefulGui(protected val plugin: Plugin) {
    abstract val rows: Int
    abstract val title: Component

    protected fun createGui(): Gui {
        return Gui.gui()
            .rows(rows)
            .title(title)
            .disableAllInteractions()
            .create()
    }

    protected fun createToggleableItem(
        currentState: Boolean,
        slot: Int,
        name: Component,
        enabledText: Component,
        disabledText: Component,
        enabledMaterial: Material = Material.GREEN_WOOL,
        disabledMaterial: Material = Material.RED_WOOL,
        onClick: (Boolean) -> CompletableFuture<Unit>
    ): GuiItem {
        var state = currentState
        var isProcessing = false
        
        return ItemBuilder.from(if (state) enabledMaterial else disabledMaterial)
            .name(name)
            .lore(if (state) enabledText else disabledText)
            .asGuiItem { event ->
                if (isProcessing) return@asGuiItem
                
                val player = event.whoClicked as Player
                isProcessing = true
                
                onClick(!state).thenAccept {
                    state = !state
                    
                    val newItem = ItemBuilder.from(if (state) enabledMaterial else disabledMaterial)
                        .name(name)
                        .lore(if (state) enabledText else disabledText)
                        .build()
                    event.inventory.setItem(slot, newItem)
                    
                    isProcessing = false
                }
            }
    }

    fun open(player: Player) {
        create(player).thenAccept { gui ->
            GuiManager.registerGui(player, gui)
            gui.open(player)
        }
    }

    abstract fun create(player: Player): CompletableFuture<Gui>
} 