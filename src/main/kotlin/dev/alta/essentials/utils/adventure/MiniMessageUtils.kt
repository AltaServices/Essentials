package dev.alta.essentials.utils.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object MiniMessageUtils {
    private val miniMessage = MiniMessage.miniMessage()
    private val legacySerializer = LegacyComponentSerializer.builder()
        .character('§')
        .hexColors()
        .build()

    fun parse(text: String, vararg placeholders: TagResolver): Component {
        return if (placeholders.isEmpty()) {
            miniMessage.deserialize(text)
        } else {
            miniMessage.deserialize(text, TagResolver.resolver(placeholders.toList()))
        }
    }

    fun Component.toLegacy(): String {
        return legacySerializer.serialize(this)
    }

    fun String.toComponent(): Component {
        return if (this.contains("§")) {
            legacySerializer.deserialize(this)
        } else {
            miniMessage.deserialize(this)
        }
    }
} 