package dev.alta.essentials.teleport

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.block.BlockFace

object Teleport {
    fun findSafeLocation(location: Location): Location {
        var loc = location.clone()
        
        // Check upward for 5 blocks
        for (y in 0..5) {
            if (isSafeLocation(loc)) return loc
            loc = loc.add(0.0, 1.0, 0.0)
        }
        
        // Reset and check downward for 5 blocks
        loc = location.clone()
        for (y in 0..5) {
            if (isSafeLocation(loc)) return loc
            loc = loc.subtract(0.0, 1.0, 0.0)
        }
        
        return location
    }

    private fun isSafeLocation(location: Location): Boolean {
        val block = location.block
        val above = block.getRelative(BlockFace.UP)
        val below = block.getRelative(BlockFace.DOWN)
        
        return !block.type.isSolid && 
               !above.type.isSolid && 
               below.type.isSolid
    }

    fun teleportSafely(player: Player, location: Location) {
        val safeLocation = findSafeLocation(location)
        player.teleport(safeLocation)
    }
} 