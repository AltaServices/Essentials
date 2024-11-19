package dev.alta.essentials.item

import dev.alta.essentials.item.builder.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Item {
    fun create(
        material: Material,
        name: String? = null,
        lore: List<String> = emptyList(),
        amount: Int = 1,
        glow: Boolean = false,
        unbreakable: Boolean = false,
        hideFlags: Boolean = false,
        builder: (ItemBuilder.() -> Unit)? = null
    ): ItemStack {
        return ItemBuilder(material).apply {
            name?.let { name(it) }
            if (lore.isNotEmpty()) {
                lore(*lore.toTypedArray())
            }
            amount(amount)
            if (glow) glow()
            if (unbreakable) unbreakable()
            if (hideFlags) hideAll()
            builder?.invoke(this)
        }.build()
    }
    
    fun builder(material: Material): ItemBuilder {
        return ItemBuilder(material)
    }
} 