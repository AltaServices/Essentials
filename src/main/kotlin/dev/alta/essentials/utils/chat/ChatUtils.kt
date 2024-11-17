package dev.alta.essentials.utils.chat

import dev.alta.essentials.Essentials
import dev.alta.essentials.utils.adventure.MiniMessageUtils.parse
import dev.alta.essentials.utils.adventure.MiniMessageUtils.toComponent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ChatUtils {
    private val configManager = Essentials.configManager
    
    fun sendMessage(sender: CommandSender, path: String, vararg placeholders: Pair<String, String>) {
        val message = getMessage(path) ?: return
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
    
    fun broadcastFromConfig(path: String, vararg placeholders: Pair<String, String>) {
        val message = getMessage(path) ?: return
        broadcastMessage(message, *placeholders)
    }
    
    private fun getMessage(path: String): String? {
        return configManager.getConfig("messages").getString(path)
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
} 