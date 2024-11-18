package dev.alta.essentials.utils.color

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

object ColorUtils {
    private val legacyColorMap = mapOf(
        '0' to NamedTextColor.BLACK,
        '1' to NamedTextColor.DARK_BLUE,
        '2' to NamedTextColor.DARK_GREEN,
        '3' to NamedTextColor.DARK_AQUA,
        '4' to NamedTextColor.DARK_RED,
        '5' to NamedTextColor.DARK_PURPLE,
        '6' to NamedTextColor.GOLD,
        '7' to NamedTextColor.GRAY,
        '8' to NamedTextColor.DARK_GRAY,
        '9' to NamedTextColor.BLUE,
        'a' to NamedTextColor.GREEN,
        'b' to NamedTextColor.AQUA,
        'c' to NamedTextColor.RED,
        'd' to NamedTextColor.LIGHT_PURPLE,
        'e' to NamedTextColor.YELLOW,
        'f' to NamedTextColor.WHITE
    )

    fun fromLegacyChar(char: Char): TextColor {
        return legacyColorMap[char.toLowerCase()] ?: NamedTextColor.WHITE
    }

    fun fromLegacyCode(code: String): TextColor {
        if (code.length != 2 || !code[0].toString().matches(Regex("[&§]"))) {
            return NamedTextColor.WHITE
        }
        return fromLegacyChar(code[1])
    }

    fun fromMiniMessage(color: String): TextColor? {
        return NamedTextColor.NAMES.value(color.toLowerCase())
    }

    fun isLegacyColorCode(text: String): Boolean {
        return text.matches(Regex("^[&§][0-9a-fA-FrRkKlLmMnNoO]$"))
    }

    fun isMiniMessageColor(text: String): Boolean {
        return text.matches(Regex("^<[a-zA-Z]+>$"))
    }

    fun stripColor(text: String): String {
        return text.replace(Regex("[&§][0-9a-fA-FrRkKlLmMnNoO]"), "")
            .replace(Regex("</?[a-zA-Z]+>"), "")
    }
} 