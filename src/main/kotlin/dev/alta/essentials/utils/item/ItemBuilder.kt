package dev.alta.essentials.utils.item

import dev.alta.essentials.utils.adventure.MiniMessageUtils.toComponent
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ItemBuilder(material: Material) {
    private val item = ItemStack(material)

    fun name(name: String): ItemBuilder {
        item.editMeta { it.displayName(name.toComponent()) }
        return this
    }

    fun lore(vararg lines: String): ItemBuilder {
        item.editMeta { 
            it.lore(lines.map { line -> line.toComponent() })
        }
        return this
    }

    fun amount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    fun enchant(enchantment: Enchantment, level: Int = 1): ItemBuilder {
        item.addUnsafeEnchantment(enchantment, level)
        return this
    }

    fun flags(vararg flags: ItemFlag): ItemBuilder {
        item.editMeta { meta ->
            flags.forEach { meta.addItemFlags(it) }
        }
        return this
    }

    fun hideAll(): ItemBuilder {
        item.editMeta { meta ->
            meta.addItemFlags(*ItemFlag.values())
        }
        return this
    }

    fun glow(): ItemBuilder {
        item.editMeta { 
            it.addEnchant(Enchantment.DURABILITY, 1, true)
            it.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
        return this
    }

    fun customModelData(data: Int): ItemBuilder {
        item.editMeta { it.setCustomModelData(data) }
        return this
    }

    fun unbreakable(state: Boolean = true): ItemBuilder {
        item.editMeta { it.isUnbreakable = state }
        return this
    }

    fun build(): ItemStack = item.clone()
} 