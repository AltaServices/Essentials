package dev.alta.essentials.time

object Time {
    private val timeUnits = mapOf(
        "year" to 31536000L,
        "years" to 31536000L,
        "y" to 31536000L,
        "month" to 2592000L,
        "months" to 2592000L,
        "mo" to 2592000L,
        "week" to 604800L,
        "weeks" to 604800L,
        "w" to 604800L,
        "day" to 86400L,
        "days" to 86400L,
        "d" to 86400L,
        "hour" to 3600L,
        "hours" to 3600L,
        "h" to 3600L,
        "minute" to 60L,
        "minutes" to 60L,
        "min" to 60L,
        "m" to 60L,
        "second" to 1L,
        "seconds" to 1L,
        "sec" to 1L,
        "s" to 1L
    )

    fun formatTime(seconds: Long): String {
        if (seconds <= 0) return "0s"

        val years = seconds / 31536000
        val months = (seconds % 31536000) / 2592000
        val weeks = (seconds % 2592000) / 604800
        val days = (seconds % 604800) / 86400
        val hours = (seconds % 86400) / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return buildString {
            if (years > 0) append("${years}y ")
            if (months > 0) append("${months}mo ")
            if (weeks > 0) append("${weeks}w ")
            if (days > 0) append("${days}d ")
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            if (remainingSeconds > 0 || isEmpty()) append("${remainingSeconds}s")
        }.trim()
    }

    fun parseTime(input: String): Long? {
        var total = 0L
        var currentNumber = ""
        var currentWord = ""
        
        fun processCurrentPair() {
            if (currentNumber.isNotEmpty() && currentWord.isNotEmpty()) {
                val number = currentNumber.toLongOrNull() ?: return
                val multiplier = timeUnits[currentWord.lowercase()] ?: return
                total += number * multiplier
                currentNumber = ""
                currentWord = ""
            }
        }

        for (char in input.replace(" ", "")) {
            when {
                char.isDigit() -> {
                    if (currentWord.isNotEmpty()) processCurrentPair()
                    currentNumber += char
                }
                char.isLetter() -> {
                    currentWord += char
                    // Check if we've found a valid unit
                    if (timeUnits.containsKey(currentWord.lowercase())) {
                        processCurrentPair()
                    }
                }
                else -> return null // Invalid character
            }
        }
        
        // Process any remaining pairs
        processCurrentPair()
        
        return if (total > 0) total else null
    }

    fun formatTimeWords(seconds: Long): String {
        if (seconds <= 0) return "0 seconds"

        val years = seconds / 31536000
        val months = (seconds % 31536000) / 2592000
        val weeks = (seconds % 2592000) / 604800
        val days = (seconds % 604800) / 86400
        val hours = (seconds % 86400) / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return buildString {
            if (years > 0) append("$years ${if (years == 1L) "year" else "years"} ")
            if (months > 0) append("$months ${if (months == 1L) "month" else "months"} ")
            if (weeks > 0) append("$weeks ${if (weeks == 1L) "week" else "weeks"} ")
            if (days > 0) append("$days ${if (days == 1L) "day" else "days"} ")
            if (hours > 0) append("$hours ${if (hours == 1L) "hour" else "hours"} ")
            if (minutes > 0) append("$minutes ${if (minutes == 1L) "minute" else "minutes"} ")
            if (remainingSeconds > 0 || isEmpty()) {
                append("$remainingSeconds ${if (remainingSeconds == 1L) "second" else "seconds"}")
            }
        }.trim()
    }

    fun isValidTimeFormat(input: String): Boolean {
        return parseTime(input) != null
    }

    fun getTimeUnitMultiplier(unit: String): Long? {
        return timeUnits[unit.lowercase()]
    }
} 