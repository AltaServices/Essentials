package dev.alta.essentials.utils.permission

import dev.alta.essentials.Essentials
import dev.alta.essentials.utils.async.AsyncUtils
import net.luckperms.api.model.user.User
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

object PermissionUtils {
    private val luckPerms = Essentials.luckPerms

    fun getUser(player: Player): CompletableFuture<User> {
        return AsyncUtils.asyncCallback {
            luckPerms.userManager.loadUser(player.uniqueId).get()
        }
    }

    fun getPrimaryGroup(player: Player): CompletableFuture<String> {
        return AsyncUtils.asyncCallback {
            luckPerms.getPlayerAdapter(Player::class.java).getUser(player).primaryGroup
        }
    }

    fun getPrefix(player: Player): CompletableFuture<String?> {
        return AsyncUtils.asyncCallback {
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