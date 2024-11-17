package dev.alta.essentials.ranks.manager

import dev.alta.essentials.Essentials
import dev.alta.essentials.ranks.Rank
import dev.alta.essentials.utils.async.AsyncUtils
import net.luckperms.api.model.group.Group
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.node.types.MetaNode
import net.luckperms.api.node.types.PermissionNode
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
            
            // Add new prefix node
            group.data().add(MetaNode.builder("prefix", prefix).build())
            luckPerms.groupManager.saveGroup(group)
            
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