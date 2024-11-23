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
        val viewers = event.viewers()
        
        // Get prefix asynchronously since we're already in an async event
        Permission.getPrefix(player).thenAccept { prefix ->
            val formattedMessage = when {
                prefix?.matches(Regex("^<[a-zA-Z]+>$")) == true -> {
                    val color = prefix.trim('<', '>')
                    "<$color>${player.name}</$color><gray>:</gray> <white>$message</white>"
                }
                prefix?.matches(Regex("^[&§][0-9a-fA-FrRkKlLmMnNoO]$")) == true -> {
                    "${prefix.replace('&', '§')}${player.name}<gray>:</gray> <white>$message</white>"
                }
                else -> {
                    val formattedPrefix = prefix ?: ""
                    "$formattedPrefix${player.name}<gray>:</gray> <white>$message</white>"
                }
            }.toComponent()
            
            // Send to all viewers in one batch instead of iterating
            viewers.forEach { it.sendMessage(formattedMessage) }
        }
    }
}
