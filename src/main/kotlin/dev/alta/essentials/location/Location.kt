package dev.alta.essentials.location

import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.Plugin
import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import org.bson.Document

object Location {
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

    fun Location.toDocument(): Document {
        return Document(mapOf(
            "world" to world.name,
            "x" to x,
            "y" to y,
            "z" to z,
            "yaw" to yaw,
            "pitch" to pitch
        ))
    }

    fun Location.toGeoJson(): Point {
        return Point(Position(x, y, z))
    }

    fun Document.toLocation(): Location? {
        return try {
            Location(
                org.bukkit.Bukkit.getWorld(getString("world")),
                getDouble("x"),
                getDouble("y"),
                getDouble("z"),
                getDouble("yaw").toFloat(),
                getDouble("pitch").toFloat()
            )
        } catch (e: Exception) {
            null
        }
    }

    fun Location.saveToConfig(plugin: Plugin, path: String) {
        plugin.config.set(path, toConfigString())
        plugin.saveConfig()
    }
} 