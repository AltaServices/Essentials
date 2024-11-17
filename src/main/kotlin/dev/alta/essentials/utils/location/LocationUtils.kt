package dev.alta.essentials.utils.location

import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection

object LocationUtils {
    fun Location.serialize(): Map<String, Any> {
        return mapOf(
            "world" to world.name,
            "x" to x,
            "y" to y,
            "z" to z,
            "yaw" to yaw,
            "pitch" to pitch
        )
    }

    fun Location.toConfigString(): String {
        return "${world.name}:$x:$y:$z:$yaw:$pitch"
    }

    fun String.toLocation(): Location? {
        val parts = split(":")
        if (parts.size != 6) return null
        
        return try {
            Location(
                org.bukkit.Bukkit.getWorld(parts[0]),
                parts[1].toDouble(),
                parts[2].toDouble(),
                parts[3].toDouble(),
                parts[4].toFloat(),
                parts[5].toFloat()
            )
        } catch (e: Exception) {
            null
        }
    }

    fun ConfigurationSection.getLocation(path: String): Location? {
        return getString(path)?.toLocation()
    }
} 