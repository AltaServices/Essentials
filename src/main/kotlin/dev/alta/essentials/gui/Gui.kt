package dev.alta.essentials.gui

import dev.alta.essentials.adventure.MiniMessage.toComponent
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import dev.triumphteam.gui.guis.PaginatedGui
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Gui {
    fun createGui(rows: Int, title: String): Gui {
        return Gui.gui()
            .rows(rows)
            .title(title.toComponent())
            .disableAllInteractions()
            .create()
    }

    fun createPaginatedGui(rows: Int, title: String): PaginatedGui {
        return Gui.paginated()
            .rows(rows)
            .title(title.toComponent())
            .disableAllInteractions()
            .create()
    }

    fun createGuiItem(
        material: Material,
        name: String? = null,
        lore: List<String> = emptyList(),
        glow: Boolean = false,
        action: ((Player, GuiItem) -> Unit)? = null
    ): GuiItem {
        return ItemBuilder.from(material)
            .apply {
                name?.let { name(it.toComponent()) }
                if (lore.isNotEmpty()) {
                    lore(*lore.map { it.toComponent() }.toTypedArray())
                }
                if (glow) {
                    glow()
                }
            }
            .asGuiItem { event ->
                val player = event.whoClicked as Player
                action?.invoke(player, event.currentItem as GuiItem)
            }
    }

    fun fillBorder(gui: Gui, item: GuiItem) {
        gui.filler.fillBorder(item)
    }

    fun fillEmpty(gui: Gui, item: GuiItem) {
        gui.filler.fill(item)
    }
} 