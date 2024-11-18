package dev.alta.essentials.permission

import dev.alta.essentials.Essentials
import dev.alta.essentials.async.Async
import net.luckperms.api.model.user.User
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

object Permission {
    private val luckPerms = Essentials.luckPerms

    fun getUser(player: Player): CompletableFuture<User> {
        return Async.asyncCallback {
            luckPerms.userManager.loadUser(player.uniqueId).get()
        }
    }

    fun getPrimaryGroup(player: Player): CompletableFuture<String> {
        return Async.asyncCallback {
            luckPerms.getPlayerAdapter(Player::class.java).getUser(player).primaryGroup
        }
    }

    fun getPrefix(player: Player): CompletableFuture<String?> {
        return Async.asyncCallback {
            luckPerms.getPlayerAdapter(Player::class.java)
                .getUser(player)
                .cachedData
                .metaData
                .prefix
        }
    }

    fun getSuffix(player: Player): String? {
        return luckPerms.getPlayerAdapter(Player::class.java)
            .getUser(player)
            .cachedData
            .metaData
            .suffix
    }
} 