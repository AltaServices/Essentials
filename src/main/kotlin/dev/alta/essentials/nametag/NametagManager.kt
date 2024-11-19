package dev.alta.essentials.nametag

import dev.alta.essentials.Essentials
import dev.alta.essentials.permission.Permission
import dev.alta.essentials.adventure.MiniMessage.toComponent
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import net.luckperms.api.event.EventBus
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.node.NodeType
import dev.alta.essentials.async.Async
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage

object NametagManager {
    private val plugin = Essentials.instance
    private val scoreboard = plugin.server.scoreboardManager.mainScoreboard
    private val miniMessage = MiniMessage.miniMessage()

    init {
        val eventBus = Essentials.luckPerms.eventBus

        eventBus.subscribe(plugin, NodeAddEvent::class.java) { event ->
            if (event.node.type == NodeType.PREFIX) {
                handlePrefixChange(event.target)
            }
        }

        eventBus.subscribe(plugin, NodeRemoveEvent::class.java) { event ->
            if (event.node.type == NodeType.PREFIX) {
                handlePrefixChange(event.target)
            }
        }
    }

    private fun handlePrefixChange(target: net.luckperms.api.model.PermissionHolder) {
        if (target is net.luckperms.api.model.group.Group) {
            Async.sync(plugin) {
                plugin.server.onlinePlayers.forEach { player ->
                    Permission.getPrimaryGroup(player).thenAccept { group ->
                        if (group == target.name) {
                            updatePlayerNametag(player)
                        }
                    }
                }
            }
        }
    }

    fun updatePlayerNametag(player: Player) {
        Permission.getPrefix(player).thenAccept { prefix ->
            val teamName = "LP${player.name}"
            var team = scoreboard.getTeam(teamName)
            
            if (team == null) {
                team = scoreboard.registerNewTeam(teamName)
            }

            val prefixComponent = when {
                prefix == null -> Component.empty()
                prefix.startsWith("<") && prefix.endsWith(">") -> {
                    try {
                        miniMessage.deserialize(prefix)
                    } catch (e: Exception) {
                        Component.text(prefix)
                    }
                }
                else -> Component.text(prefix)
            }

            team.prefix(prefixComponent)
            
            // Extract color from prefix component if possible
            val color = extractColor(prefixComponent)
            team.color(color as? NamedTextColor)
            
            // Add player to team
            team.addEntry(player.name)
            
            // Update display name
            val displayName = prefixComponent.append(Component.text(player.name))
            player.displayName(displayName)
        }
    }

    private fun extractColor(component: Component): TextColor? {
        return component.color() ?: 
               (component as? net.kyori.adventure.text.TextComponent)?.children()
                   ?.firstOrNull()?.color()
    }

    fun removePlayerNametag(player: Player) {
        Async.sync(plugin) {
            val teamName = "LP${player.name}"
            scoreboard.getTeam(teamName)?.apply {
                removeEntry(player.name)
                unregister()
            }
        }
    }

    fun reloadAllNametags() {
        Async.sync(plugin) {
            plugin.server.onlinePlayers.forEach { player ->
                updatePlayerNametag(player)
            }
        }
    }
}