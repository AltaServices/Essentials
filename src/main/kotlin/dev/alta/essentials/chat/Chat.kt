package dev.alta.essentials.chat

import dev.alta.essentials.Essentials
import dev.alta.essentials.adventure.MiniMessage.parse
import dev.alta.essentials.adventure.MiniMessage.toComponent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Chat {
    private const val PREFIX = "<gray>[<gradient:gold:yellow>Essentials</gradient>]</gray>"
    
    private object Messages {
        const val NO_PERMISSION = "$PREFIX <red>You don't have permission to use this commands!"
        const val PLAYER_ONLY = "$PREFIX <red>This commands can only be used by players!"
        const val PLAYER_NOT_FOUND = "$PREFIX <red>Player not found!"
        const val INVALID_ARGUMENTS = "$PREFIX <red>Invalid arguments! Usage: <usage>"
    }
    
    fun sendMessage(sender: CommandSender, path: String, vararg placeholders: Pair<String, String>) {
        val message = when(path) {
            "errors.no-permission" -> Messages.NO_PERMISSION
            "errors.player-only" -> Messages.PLAYER_ONLY
            "errors.player-not-found" -> Messages.PLAYER_NOT_FOUND
            "errors.invalid-arguments" -> Messages.INVALID_ARGUMENTS
            else -> return
        }
        
        val resolvers = placeholders.map { (key, value) ->
            Placeholder.parsed(key, value)
        }
        
        val component = parse(message, TagResolver.resolver(resolvers))
        when (sender) {
            is Player -> sender.sendMessage(component)
            else -> sender.sendMessage(component)
        }
    }
    
    fun broadcastMessage(message: String, vararg placeholders: Pair<String, String>) {
        val resolvers = placeholders.map { (key, value) ->
            Placeholder.parsed(key, value)
        }
        
        val component = parse(message, TagResolver.resolver(resolvers))
        Essentials.instance.server.sendMessage(component)
    }
    
    fun Audience.sendMiniMessage(message: String) {
        this.sendMessage(message.toComponent())
    }
    
    fun Component.append(other: Component): Component {
        return this.append(other)
    }
    
    fun Component.append(text: String): Component {
        return this.append(text.toComponent())
    }
    
    fun formatChat(player: String, message: String): String {
        return "$player: $message"
    }
} 