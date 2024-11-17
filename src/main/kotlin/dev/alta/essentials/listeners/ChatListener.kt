package dev.alta.essentials.listeners

import dev.alta.essentials.utils.adventure.MiniMessageUtils.toComponent
import dev.alta.essentials.utils.annotations.register.AutoRegister
import dev.alta.essentials.utils.chat.ChatUtils
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

@AutoRegister
class ChatListener : Listener {
    
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onChat(event: AsyncChatEvent) {
        event.isCancelled = true
        
        val player = event.player
        val message = PlainTextComponentSerializer.plainText().serialize(event.message())
        val formattedMessage = ChatUtils.formatChat(player.name, message)
        
        event.viewers().forEach { viewer ->
            viewer.sendMessage(formattedMessage.toComponent())
        }
    }
}
