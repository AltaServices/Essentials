package dev.alta.essentials.teleport

import org.bukkit.Location
import org.bukkit.entity.Player
import dev.alta.essentials.async.Async
import dev.alta.essentials.player.Player.saveLastLocation
import java.util.concurrent.CompletableFuture

object Teleport {
    private val teleportRequests = mutableMapOf<Player, MutableMap<Player, Long>>()
    
    fun teleport(player: Player, target: Location, delay: Long = 0): CompletableFuture<Boolean> {
        return CompletableFuture<Boolean>().also { future ->
            val startLoc = player.location.clone()
            
            if (delay > 0) {
                player.sendMessage("Teleporting in ${delay/20} seconds. Don't move!")
            }
            
            Async.later(delay) {
                if (player.location.distance(startLoc) > 0.5) {
                    player.sendMessage("Teleport cancelled - you moved!")
                    future.complete(false)
                    return@later
                }
                
                player.saveLastLocation()
                player.teleport(target)
                future.complete(true)
            }
        }
    }
    
    fun requestTeleport(sender: Player, target: Player, timeout: Long = 60000): CompletableFuture<Boolean> {
        teleportRequests.getOrPut(target) { mutableMapOf() }[sender] = System.currentTimeMillis() + timeout
        target.sendMessage("${sender.name} has requested to teleport to you. Type /tpaccept to accept.")
        
        return CompletableFuture<Boolean>().also { future ->
            Async.later(timeout) {
                if (!future.isDone) {
                    teleportRequests[target]?.remove(sender)
                    future.complete(false)
                }
            }
        }
    }
} 