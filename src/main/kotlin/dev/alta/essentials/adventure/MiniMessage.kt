package dev.alta.essentials.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object MiniMessage {
    private const val PREFIX = "<gray>[<gradient:gold:yellow>Essentials</gradient>]</gray>"
    private val miniMessage = MiniMessage.miniMessage()
    private val legacySerializer = LegacyComponentSerializer.builder()
        .character('§')
        .hexColors()
        .build()

    private val globalResolver = TagResolver.resolver(
        Placeholder.parsed("prefix", PREFIX)
    )

    fun parse(text: String, vararg placeholders: TagResolver): Component {
        val resolvers = if (placeholders.isEmpty()) {
            globalResolver
        } else {
            TagResolver.resolver(listOf(globalResolver) + placeholders)
        }
        return miniMessage.deserialize(text, resolvers)
    }

    fun Component.toLegacy(): String {
        return legacySerializer.serialize(this)
    }

    fun String.toComponent(): Component {
        return if (this.contains("§")) {
            legacySerializer.deserialize(this)
        } else {
            parse(this)
        }
    }
} 