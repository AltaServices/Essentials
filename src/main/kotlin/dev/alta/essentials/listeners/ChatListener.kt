package dev.alta.essentials.listeners

import dev.alta.essentials.adventure.MiniMessage.toComponent
import dev.alta.essentials.annotations.register.AutoRegister
import dev.alta.essentials.annotations.listener.Listener
import dev.alta.essentials.permission.Permission
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener as BukkitListener

@Listener
class ChatListener : BukkitListener {
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        if (event.isCancelled) return
        
        event.isCancelled = true
        
        val player = event.player
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())
        
        // Get prefix asynchronously since we're already in an async event
        Permission.getPrefix(player).thenAccept { prefix ->
            val formattedMessage = when {
                // For MiniMessage color tags
                prefix?.matches(Regex("^<[a-zA-Z]+>$")) == true -> {
                    val color = prefix.trim('<', '>')
                    "<$color>${player.name}</$color><gray>:</gray> <white>$message</white>"
                }
                // For legacy color codes
                prefix?.matches(Regex("^[&§][0-9a-fA-FrRkKlLmMnNoO]$")) == true -> {
                    "${prefix.replace('&', '§')}${player.name}<gray>:</gray> <white>$message</white>"
                }
                // For regular prefixes or no prefix
                else -> {
                    val formattedPrefix = prefix ?: ""
                    "$formattedPrefix${player.name}<gray>:</gray> <white>$message</white>"
                }
            }
            
            event.viewers().forEach { viewer ->
                viewer.sendMessage(formattedMessage.toComponent())
            }
        }
    }
}
