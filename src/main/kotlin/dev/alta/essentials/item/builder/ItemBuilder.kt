package dev.alta.essentials.item.builder

import dev.alta.essentials.adventure.MiniMessage.toComponent
import dev.triumphteam.gui.builder.item.ItemBuilder as TriumphItemBuilder
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ItemBuilder(material: Material) {
    private val builder = TriumphItemBuilder.from(material)

    fun name(name: String): ItemBuilder {
        builder.name(name.toComponent())
        return this
    }

    fun lore(vararg lines: String): ItemBuilder {
        builder.lore(*lines.map { it.toComponent() }.toTypedArray())
        return this
    }

    fun amount(amount: Int): ItemBuilder {
        builder.amount(amount)
        return this
    }

    fun enchant(enchantment: Enchantment, level: Int = 1): ItemBuilder {
        builder.enchant(enchantment, level)
        return this
    }

    fun flags(vararg flags: ItemFlag): ItemBuilder {
        builder.flags(*flags)
        return this
    }

    fun hideAll(): ItemBuilder {
        builder.flags(*ItemFlag.values())
        return this
    }

    fun glow(): ItemBuilder {
        builder.glow()
        return this
    }

    fun customModelData(data: Int): ItemBuilder {
        builder.model(data)
        return this
    }

    fun unbreakable(state: Boolean = true): ItemBuilder {
        builder.unbreakable(state)
        return this
    }

    fun build(): ItemStack = builder.build()
}