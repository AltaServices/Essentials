package dev.alta.essentials.utils.command

import dev.alta.essentials.utils.chat.ChatUtils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CommandUtils {
    fun requirePlayer(sender: CommandSender): Player? {
        if (sender !is Player) {
            ChatUtils.sendMessage(sender, "errors.player-only")
            return null
        }
        return sender
    }

    fun getTargetPlayer(sender: CommandSender, name: String): Player? {
        val player = Bukkit.getPlayer(name)
        if (player == null) {
            ChatUtils.sendMessage(sender, "errors.player-not-found")
            return null
        }
        return player
    }

    fun checkPermission(sender: CommandSender, permission: String): Boolean {
        if (!sender.hasPermission(permission)) {
            ChatUtils.sendMessage(sender, "errors.no-permission")
            return false
        }
        return true
    }
} 