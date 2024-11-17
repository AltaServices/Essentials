package dev.alta.essentials.utils.nametag

import dev.alta.essentials.Essentials
import dev.alta.essentials.utils.adventure.MiniMessageUtils.toComponent
import dev.alta.essentials.utils.async.AsyncUtils
import net.kyori.adventure.text.Component
import net.luckperms.api.LuckPerms
import net.luckperms.api.event.user.UserDataRecalculateEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import java.util.*

object NametagManager {
    private val plugin = Essentials.instance
    private val luckPerms: LuckPerms = Essentials.luckPerms
    private val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    private val teamPriorities = mutableMapOf<String, Int>()
    private val playerTeams = mutableMapOf<UUID, String>()

    init {
        setupGroupPriorities()
        registerLuckPermsListener()
    }

    private fun setupGroupPriorities() {
        val groups = luckPerms.groupManager.loadedGroups
        groups.forEach { group ->
            val weight = group.weight.orElse(0)
            val teamName = "LP${weight.toString().padStart(3, '0')}${group.name}"
            teamPriorities[teamName] = weight
            
            getOrCreateTeam(teamName).apply {
                prefix(Component.text(group.cachedData.metaData.prefix ?: ""))
                suffix(Component.text(group.cachedData.metaData.suffix ?: ""))
            }
        }
    }

    private fun registerLuckPermsListener() {
        luckPerms.eventBus.subscribe(plugin, UserDataRecalculateEvent::class.java) { event ->
            val player = Bukkit.getPlayer(event.user.uniqueId) ?: return@subscribe
            updatePlayerNametag(player)
        }
    }

    fun updatePlayerNametag(player: Player) {
        AsyncUtils.async {
            val user = luckPerms.getPlayerAdapter(Player::class.java).getUser(player)
            val group = user.primaryGroup
            val weight = luckPerms.groupManager.getGroup(group)?.weight?.orElse(0) ?: 0
            val teamName = "LP${weight.toString().padStart(3, '0')}${group}"

            AsyncUtils.sync {
                // Remove from old team
                playerTeams[player.uniqueId]?.let { oldTeam ->
                    scoreboard.getTeam(oldTeam)?.removeEntry(player.name)
                }

                // Add to new team
                getOrCreateTeam(teamName).addEntry(player.name)
                playerTeams[player.uniqueId] = teamName
            }
        }
    }

    fun setCustomNametag(player: Player, prefix: String? = null, suffix: String? = null, priority: Int) {
        val teamName = "CUSTOM${priority.toString().padStart(3, '0')}${player.name}"
        
        // Remove from old team
        playerTeams[player.uniqueId]?.let { oldTeam ->
            scoreboard.getTeam(oldTeam)?.removeEntry(player.name)
        }

        // Create and setup new team
        getOrCreateTeam(teamName).apply {
            prefix(prefix?.toComponent() ?: Component.empty())
            suffix(suffix?.toComponent() ?: Component.empty())
            addEntry(player.name)
        }
        
        teamPriorities[teamName] = priority
        playerTeams[player.uniqueId] = teamName
    }

    private fun getOrCreateTeam(name: String): Team {
        return scoreboard.getTeam(name) ?: scoreboard.registerNewTeam(name)
    }

    fun removeCustomNametag(player: Player) {
        playerTeams[player.uniqueId]?.let { teamName ->
            if (teamName.startsWith("CUSTOM")) {
                scoreboard.getTeam(teamName)?.let { team ->
                    team.removeEntry(player.name)
                    team.unregister()
                }
                playerTeams.remove(player.uniqueId)
                teamPriorities.remove(teamName)
                updatePlayerNametag(player)
            }
        }
    }
} 