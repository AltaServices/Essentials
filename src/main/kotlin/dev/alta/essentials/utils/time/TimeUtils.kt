package dev.alta.essentials.utils.time

object TimeUtils {
    fun formatTime(seconds: Long): String {
        if (seconds < 60) return "${seconds}s"
        
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        if (minutes < 60) return "${minutes}m ${remainingSeconds}s"
        
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        if (hours < 24) return "${hours}h ${remainingMinutes}m ${remainingSeconds}s"
        
        val days = hours / 24
        val remainingHours = hours % 24
        return "${days}d ${remainingHours}h ${remainingMinutes}m ${remainingSeconds}s"
    }

    fun parseTime(input: String): Long? {
        var total = 0L
        var current = ""
        
        for (char in input.lowercase()) {
            when (char) {
                'd' -> {
                    total += (current.toLongOrNull() ?: return null) * 86400
                    current = ""
                }
                'h' -> {
                    total += (current.toLongOrNull() ?: return null) * 3600
                    current = ""
                }
                'm' -> {
                    total += (current.toLongOrNull() ?: return null) * 60
                    current = ""
                }
                's' -> {
                    total += current.toLongOrNull() ?: return null
                    current = ""
                }
                in '0'..'9' -> current += char
                else -> return null
            }
        }
        return total
    }
} 