package dev.alta.essentials.ranks.manager

import dev.alta.essentials.Essentials
import dev.alta.essentials.ranks.Rank
import dev.alta.essentials.utils.async.AsyncUtils
import dev.alta.essentials.utils.nametag.NametagManager
import dev.alta.essentials.utils.permission.PermissionUtils
import net.luckperms.api.model.group.Group
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.node.types.MetaNode
import net.luckperms.api.node.types.PermissionNode
import net.luckperms.api.node.types.PrefixNode
import java.util.concurrent.CompletableFuture

object RankManager {
    private val luckPerms = Essentials.luckPerms

    fun createRank(name: String, weight: Int = 0): CompletableFuture<Boolean> {
        return AsyncUtils.asyncCallback {
            if (luckPerms.groupManager.getGroup(name) != null) {
                return@asyncCallback false
            }

            val group = luckPerms.groupManager.createAndLoadGroup(name).get()
            group.data().add(Node.builder("group.$name").build())
            group.data().add(MetaNode.builder("weight", weight.toString()).build())
            
            luckPerms.groupManager.saveGroup(group)
            true
        }
    }

    fun deleteRank(name: String): CompletableFuture<Boolean> {
        return AsyncUtils.asyncCallback {
            val group = luckPerms.groupManager.getGroup(name) ?: return@asyncCallback false
            luckPerms.groupManager.deleteGroup(group)
            true
        }
    }

    fun setRankWeight(name: String, weight: Int): CompletableFuture<Boolean> {
        return AsyncUtils.asyncCallback {
            val group = luckPerms.groupManager.getGroup(name) ?: return@asyncCallback false
            
            // Remove old weight nodes
            group.data().clear(NodeType.META.predicate { it.metaKey == "weight" })
            
            // Add new weight node
            group.data().add(MetaNode.builder("weight", weight.toString()).build())
            luckPerms.groupManager.saveGroup(group)
            
            true
        }
    }

    fun setRankPrefix(name: String, prefix: String): CompletableFuture<Boolean> {
        return AsyncUtils.asyncCallback {
            val group = luckPerms.groupManager.getGroup(name) ?: return@asyncCallback false
            
            // Remove old prefix nodes
            group.data().clear(NodeType.PREFIX::matches)
            
            // Process the prefix
            val processedPrefix = when {
                // If it starts with & or § and is followed by a single character, treat as color
                prefix.matches(Regex("^[&§][0-9a-fA-FrRkKlLmMnNoO]$")) -> {
                    // Convert & to § if needed
                    prefix.replace('&', '§')
                }
                // If it's a MiniMessage color tag
                prefix.matches(Regex("^<[a-zA-Z]+>$")) -> {
                    // Just use the color tag itself
                    prefix
                }
                // Otherwise use as-is
                else -> prefix.trim('"', '\'')
            }
            
            // Add new prefix node with priority 100
            group.data().add(PrefixNode.builder()
                .prefix(processedPrefix)
                .priority(100)
                .build())
            
            luckPerms.groupManager.saveGroup(group)
            
            // Force update nametags
            AsyncUtils.sync {
                Essentials.instance.server.onlinePlayers.forEach { player ->
                    PermissionUtils.getPrimaryGroup(player).thenAccept { group ->
                        if (group == name) {
                            NametagManager.updatePlayerNametag(player)
                        }
                    }
                }
            }
            
            true
        }
    }

    fun addRankPermission(name: String, permission: String): CompletableFuture<Boolean> {
        return AsyncUtils.asyncCallback {
            val group = luckPerms.groupManager.getGroup(name) ?: return@asyncCallback false
            
            group.data().add(PermissionNode.builder(permission).build())
            luckPerms.groupManager.saveGroup(group)
            
            true
        }
    }

    fun removeRankPermission(name: String, permission: String): CompletableFuture<Boolean> {
        return AsyncUtils.asyncCallback {
            val group = luckPerms.groupManager.getGroup(name) ?: return@asyncCallback false
            
            group.data().remove(PermissionNode.builder(permission).build())
            luckPerms.groupManager.saveGroup(group)
            
            true
        }
    }

    fun setRankParent(name: String, parent: String): CompletableFuture<Boolean> {
        return AsyncUtils.asyncCallback {
            val group = luckPerms.groupManager.getGroup(name) ?: return@asyncCallback false
            val parentGroup = luckPerms.groupManager.getGroup(parent) ?: return@asyncCallback false
            
            // Remove old inheritance nodes
            group.data().clear(NodeType.INHERITANCE::matches)
            
            // Add new inheritance node
            group.data().add(InheritanceNode.builder(parentGroup).build())
            luckPerms.groupManager.saveGroup(group)
            
            true
        }
    }

    fun getRankInfo(name: String): CompletableFuture<Rank?> {
        return AsyncUtils.asyncCallback {
            val group = luckPerms.groupManager.getGroup(name) ?: return@asyncCallback null

            Rank(
                name = group.name,
                weight = group.weight.orElse(0),
                prefix = group.cachedData.metaData.prefix,
                permissions = group.nodes.filterIsInstance<PermissionNode>().map { it.permission },
                parents = group.nodes.filterIsInstance<InheritanceNode>().map { it.groupName }
            )
        }
    }

    fun getAllRanks(): CompletableFuture<List<Group>> {
        return AsyncUtils.asyncCallback {
            luckPerms.groupManager.loadedGroups.sortedByDescending { it.weight.orElse(0) }
        }
    }
}