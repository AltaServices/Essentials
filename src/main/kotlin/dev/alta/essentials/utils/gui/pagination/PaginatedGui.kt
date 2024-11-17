package dev.alta.essentials.utils.gui.pagination

import dev.alta.essentials.utils.gui.Gui
import dev.alta.essentials.utils.gui.builder.GuiBuilder
import dev.alta.essentials.utils.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PaginatedGui(
    private val title: String,
    private val rows: Int,
    private val items: List<ItemStack>,
    private val itemsPerPage: Int = (rows - 2) * 7
) {
    private var currentPage = 0
    private val maxPage = (items.size - 1) / itemsPerPage

    fun build(): Gui {
        return GuiBuilder(rows, title)
            .fillBorder(ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build())
            .apply {
                // Add items for current page
                val startIndex = currentPage * itemsPerPage
                val endIndex = minOf(startIndex + itemsPerPage, items.size)

                for (i in startIndex until endIndex) {
                    val slot = getSlot(i - startIndex)
                    setItem(slot, items[i])
                }

                // Navigation buttons
                if (currentPage > 0) {
                    setItem(rows * 9 - 9, ItemBuilder(Material.ARROW)
                        .name("<yellow>Previous Page")
                        .build()) { previousPage(it.whoClicked as Player) }
                }

                if (currentPage < maxPage) {
                    setItem(rows * 9 - 1, ItemBuilder(Material.ARROW)
                        .name("<yellow>Next Page")
                        .build()) { nextPage(it.whoClicked as Player) }
                }
            }
            .build()
    }

    private fun getSlot(index: Int): Int {
        val row = index / 7 + 1
        val col = index % 7 + 1
        return row * 9 + col
    }

    private fun nextPage(player: Player) {
        if (currentPage < maxPage) {
            currentPage++
            build().open(player)
        }
    }

    private fun previousPage(player: Player) {
        if (currentPage > 0) {
            currentPage--
            build().open(player)
        }
    }
}