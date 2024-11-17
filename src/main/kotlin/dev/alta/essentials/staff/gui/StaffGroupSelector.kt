package dev.alta.essentials.staff.gui

import dev.alta.essentials.Essentials
import dev.alta.essentials.staff.collection.StaffSettings
import dev.alta.essentials.utils.async.AsyncUtils
import dev.alta.essentials.utils.chat.ChatUtils
import dev.alta.essentials.utils.chat.ChatUtils.sendMiniMessage
import dev.alta.essentials.utils.gui.Gui
import dev.alta.essentials.utils.gui.builder.GuiBuilder
import dev.alta.essentials.utils.gui.manager.GuiManager
import dev.alta.essentials.utils.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

class StaffGroupSelector(private val player: Player) {
    private val luckPerms = Essentials.luckPerms
    private var currentGui: Gui? = null

    fun build(): Gui {
        return GuiBuilder(6, "Staff Group Selector")
            .apply {
                val groups = luckPerms.groupManager.loadedGroups.sortedByDescending { it.weight.orElse(0) }

                groups.forEachIndexed { index, group ->
                    if (index >= 54) return@forEachIndexed

                    val isStaffGroup = StaffSettings.isStaffGroup(group.name)
                    val item = ItemBuilder(if (isStaffGroup) Material.LIME_WOOL else Material.RED_WOOL)
                        .name("§f${group.name}")
                        .lore(
                            "§7Weight: §f${group.weight.orElse(0)}",
                            "",
                            if (isStaffGroup) "§cClick to remove as staff group"
                            else "§aClick to set as staff group"
                        )
                        .build()

                    setItem(index, item) { event ->
                        event.isCancelled = true
                        val groupName = group.name
                        val slot = event.slot
                        
                        AsyncUtils.async {
                            if (StaffSettings.isStaffGroup(groupName)) {
                                StaffSettings.removeStaffGroup(groupName)
                                player.sendMiniMessage("<gray>[<gradient:gold:yellow>Essentials</gradient>]</gray> <red>Successfully removed <white>$groupName</white> from staff groups!")
                            } else {
                                StaffSettings.addStaffGroup(groupName)
                                player.sendMiniMessage("<gray>[<gradient:gold:yellow>Essentials</gradient>]</gray> <green>Successfully added <white>$groupName</white> as a staff group!")
                            }
                            
                            AsyncUtils.sync {
                                // Update the clicked item immediately
                                val newItem = ItemBuilder(if (!StaffSettings.isStaffGroup(groupName)) Material.RED_WOOL else Material.LIME_WOOL)
                                    .name("§f${groupName}")
                                    .lore(
                                        "§7Weight: §f${group.weight.orElse(0)}",
                                        "",
                                        if (StaffSettings.isStaffGroup(groupName)) "§cClick to remove as staff group"
                                        else "§aClick to set as staff group"
                                    )
                                    .build()
                                
                                event.inventory.setItem(slot, newItem)
                                
                                // Update the GUI registration
                                val newGui = build()
                                GuiManager.updateGui(player, newGui)
                                currentGui = newGui
                            }
                        }
                    }
                }
            }
            .build()
            .also { currentGui = it }
    }

    fun open() {
        if (currentGui == null) {
            currentGui = build()
        }
        currentGui?.open(player)
    }
} 