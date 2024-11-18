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
import dev.alta.essentials.color.Color
import net.kyori.adventure.text.format.NamedTextColor

object NametagManager {
    private val scoreboard = Essentials.instance.server.scoreboardManager.mainScoreboard

    init {
        // Register LuckPerms event listeners
        val eventBus: EventBus = Essentials.luckPerms.eventBus

        // Listen for prefix changes (additions)
        eventBus.subscribe(Essentials.instance, NodeAddEvent::class.java) { event ->
            if (event.node.type == NodeType.PREFIX) {
                handlePrefixChange(event.target)
            }
        }

        // Listen for prefix changes (removals)
        eventBus.subscribe(Essentials.instance, NodeRemoveEvent::class.java) { event ->
            if (event.node.type == NodeType.PREFIX) {
                handlePrefixChange(event.target)
            }
        }
    }

    private fun handlePrefixChange(target: net.luckperms.api.model.PermissionHolder) {
        if (target is net.luckperms.api.model.group.Group) {
            // If a group's prefix changed, update all players in that group
            Async.sync {
                Essentials.instance.server.onlinePlayers.forEach { player ->
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
            
            // Process the prefix and determine team color
            when {
                // For MiniMessage color tags
                prefix?.matches(Regex("^<[a-zA-Z]+>$")) == true -> {
                    val color = prefix.trim('<', '>')
                    // Set empty prefix since we're using team color
                    team.prefix(Component.empty())
                    // Set team color using the parsed color
                    team.color(net.kyori.adventure.text.format.NamedTextColor.NAMES.value(color))
                }
                // For legacy color codes
                prefix?.matches(Regex("^[&§][0-9a-fA-FrRkKlLmMnNoO]$")) == true -> {
                    // Set empty prefix since we're using team color
                    team.prefix(Component.empty())
                    // Convert legacy color code to team color
                    val colorChar = prefix.last().toLowerCase()
                    val color = Color.fromLegacyChar(colorChar)
                    team.color(color as? NamedTextColor)
                }
                // For regular prefixes
                else -> {
                    team.prefix(prefix?.toComponent() ?: Component.empty())
                    team.color(null) // Reset team color
                }
            }
            
            // Add player to team
            team.addEntry(player.name)
            
            // Update player's display name with same color logic
            val displayName = when {
                // For MiniMessage color tags
                prefix?.matches(Regex("^<[a-zA-Z]+>$")) == true -> {
                    val color = prefix.trim('<', '>')
                    "<$color>${player.name}</$color>".toComponent()
                }
                // For legacy color codes
                prefix?.matches(Regex("^[&§][0-9a-fA-FrRkKlLmMnNoO]$")) == true -> {
                    Component.text("${prefix.replace('&', '§')}${player.name}")
                }
                // For regular prefixes
                else -> {
                    prefix?.toComponent()?.append(Component.text(player.name)) 
                        ?: Component.text(player.name)
                }
            }
            
            player.displayName(displayName)
        }
    }

    fun removePlayerNametag(player: Player) {
        val teamName = "LP${player.name}"
        val team = scoreboard.getTeam(teamName)
        team?.removeEntry(player.name)
        team?.unregister()
    }

    fun reloadAllNametags() {
        Essentials.instance.server.onlinePlayers.forEach { player ->
            updatePlayerNametag(player)
        }
    }
}