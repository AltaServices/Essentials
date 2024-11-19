package dev.alta.essentials.config

data class Config(
    val name: String,
    val copyDefaults: Boolean = true,
    val autoSave: Boolean = true,
    val saveInterval: Long = 300, // 5 minutes in seconds
    val useAsyncSaving: Boolean = true,
    val createIfNotExists: Boolean = true,
    val reloadAutomatically: Boolean = false
) 