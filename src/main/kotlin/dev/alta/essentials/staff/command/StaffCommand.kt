package dev.alta.essentials.staff.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import dev.alta.essentials.staff.gui.StaffGroupSelector
import dev.alta.essentials.utils.command.CommandUtils
import dev.alta.essentials.utils.annotations.register.AutoRegister
import org.bukkit.command.CommandSender

@AutoRegister
@CommandAlias("setstaff")
@CommandPermission("essentials.commands.setstaff")
class StaffCommand : BaseCommand() {
    
    @Default
    fun onCommand(sender: CommandSender) {
        val player = CommandUtils.requirePlayer(sender) ?: return
        StaffGroupSelector(player).open()
    }
} 