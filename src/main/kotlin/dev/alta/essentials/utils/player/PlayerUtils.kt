package dev.alta.essentials.utils.player

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import dev.alta.essentials.utils.chat.ChatUtils.sendMiniMessage

object PlayerUtils {
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
} 