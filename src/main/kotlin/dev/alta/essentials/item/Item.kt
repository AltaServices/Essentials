package dev.alta.essentials.item

import dev.alta.essentials.adventure.MiniMessage.toComponent
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object Item {
    fun create(material: Material, builder: ItemStack.() -> Unit = {}): ItemStack {
        return ItemStack(material).apply(builder)
    }

    fun ItemStack.name(name: String): ItemStack {
        editMeta { it.displayName(name.toComponent()) }
        return this
    }

    fun ItemStack.lore(vararg lines: String): ItemStack {
        editMeta { 
            it.lore(lines.map { line -> line.toComponent() })
        }
        return this
    }

    fun ItemStack.lore(lines: List<String>): ItemStack {
        editMeta { 
            it.lore(lines.map { line -> line.toComponent() })
        }
        return this
    }

    fun ItemStack.enchant(enchantment: Enchantment, level: Int = 1): ItemStack {
        addUnsafeEnchantment(enchantment, level)
        return this
    }

    fun ItemStack.flags(vararg flags: ItemFlag): ItemStack {
        editMeta { meta ->
            flags.forEach { meta.addItemFlags(it) }
        }
        return this
    }

    fun ItemStack.hideAll(): ItemStack {
        editMeta { meta ->
            meta.addItemFlags(*ItemFlag.values())
        }
        return this
    }

    fun ItemStack.amount(amount: Int): ItemStack {
        this.amount = amount
        return this
    }

    fun ItemStack.glow(): ItemStack {
        editMeta { 
            it.addEnchant(Enchantment.DURABILITY, 1, true)
            it.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
        return this
    }

    fun ItemStack.customModelData(data: Int): ItemStack {
        editMeta { it.setCustomModelData(data) }
        return this
    }

    fun ItemStack.unbreakable(state: Boolean = true): ItemStack {
        editMeta { it.isUnbreakable = state }
        return this
    }
} 