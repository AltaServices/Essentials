package dev.alta.essentials.config

import dev.alta.essentials.Essentials
import dev.alta.essentials.async.Async
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

class ConfigManager(private val plugin: Essentials) {
    private val configs = mutableMapOf<String, FileConfiguration>()
    private val configFiles = mutableMapOf<String, File>()

    init {
        // Save default config.yml if it doesn't exist
        plugin.saveDefaultConfig()
        // Load the main config
        configs["config"] = plugin.config
        configFiles["config"] = File(plugin.dataFolder, "config.yml")
    }

    fun createConfig(name: String, copyDefaults: Boolean = true) {
        val file = File(plugin.dataFolder, "$name.yml")

        if (!file.exists()) {
            file.parentFile.mkdirs()
            if (copyDefaults) {
                plugin.saveResource("$name.yml", false)
            } else {
                file.createNewFile()
            }
        }

        val config = YamlConfiguration.loadConfiguration(file)
        configs[name] = config
        configFiles[name] = file
    }

    fun getConfig(name: String = "config"): FileConfiguration {
        return configs[name] ?: throw IllegalArgumentException("Config $name not found!")
    }

    fun saveConfig(name: String = "config", async: Boolean = true) {
        val config = configs[name] ?: throw IllegalArgumentException("Config $name not found!")
        val file = configFiles[name] ?: throw IllegalArgumentException("Config file $name not found!")

        if (async) {
            Async.async {
                try {
                    config.save(file)
                } catch (e: IOException) {
                    Async.sync {
                        plugin.logger.severe("Could not save config $name: ${e.message}")
                    }
                }
            }
        } else {
            try {
                config.save(file)
            } catch (e: IOException) {
                plugin.logger.severe("Could not save config $name: ${e.message}")
            }
        }
    }

    fun reloadConfig(name: String = "config") {
        if (name == "config") {
            plugin.reloadConfig()
            configs["config"] = plugin.config
            return
        }

        val file = configFiles[name] ?: throw IllegalArgumentException("Config file $name not found!")
        configs[name] = YamlConfiguration.loadConfiguration(file)
    }

    fun reloadAllConfigs() {
        configs.keys.forEach { reloadConfig(it) }
    }

    fun saveAllConfigs(async: Boolean = true) {
        configs.keys.forEach { saveConfig(it, async) }
    }
}