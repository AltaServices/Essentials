package dev.alta.essentials.player

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import dev.alta.essentials.chat.Chat.sendMiniMessage
import org.bukkit.Location
import java.util.UUID

object Player {
    private val cooldowns = mutableMapOf<UUID, MutableMap<String, Long>>()
    private val lastLocations = mutableMapOf<UUID, Location>()
    
    fun Player.hasCooldown(key: String): Boolean {
        val playerCooldowns = cooldowns.getOrDefault(uniqueId, mutableMapOf())
        val lastUse = playerCooldowns[key] ?: return false
        return System.currentTimeMillis() - lastUse < 0
    }
    
    fun Player.setCooldown(key: String, durationMillis: Long) {
        cooldowns.getOrPut(uniqueId) { mutableMapOf() }[key] = 
            System.currentTimeMillis() + durationMillis
    }
    
    fun Player.saveLastLocation() {
        lastLocations[uniqueId] = location.clone()
    }
    
    fun Player.getLastLocation(): Location? = lastLocations[uniqueId]
    
    fun Player.reset() {
        inventory.clear()
        enderChest.clear()
        activePotionEffects.clear()
        foodLevel = 20
        health = 20.0
        exp = 0f
        level = 0
        gameMode = GameMode.SURVIVAL
        allowFlight = false
        isFlying = false
    }

    fun Player.heal() {
        health = maxHealth
        foodLevel = 20
        saturation = 20f
        fireTicks = 0
        activePotionEffects.clear()
    }

    fun Player.giveOrDrop(vararg items: ItemStack) {
        val remaining = inventory.addItem(*items)
        remaining.values.forEach { world.dropItemNaturally(location, it) }
    }

    fun Player.sendActionBar(message: String) {
        sendMiniMessage(message)
    }

    fun Player.isVanished(): Boolean {
        return hasMetadata("vanished")
    }
} 