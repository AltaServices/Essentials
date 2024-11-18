package dev.alta.essentials.command

import dev.alta.essentials.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Command {
    fun requirePlayer(sender: CommandSender): Player? {
        if (sender !is Player) {
            Chat.sendMessage(sender, "errors.player-only")
            return null
        }
        return sender
    }

    fun getTargetPlayer(sender: CommandSender, name: String): Player? {
        val player = Bukkit.getPlayer(name)
        if (player == null) {
            Chat.sendMessage(sender, "errors.player-not-found")
            return null
        }
        return player
    }

    fun checkPermission(sender: CommandSender, permission: String): Boolean {
        if (!sender.hasPermission(permission)) {
            Chat.sendMessage(sender, "errors.no-permission")
            return false
        }
        return true
    }
} 