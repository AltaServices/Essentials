package dev.alta.essentials.config.manager

import dev.alta.essentials.async.Async
import dev.alta.essentials.config.Config
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class ConfigManager(private val plugin: Plugin) {
    private val configs = ConcurrentHashMap<String, FileConfiguration>()
    private val configFiles = ConcurrentHashMap<String, File>()
    private val configSettings = ConcurrentHashMap<String, Config>()
    private val autoSaveTasks = ConcurrentHashMap<String, Int>()

    init {
        // Initialize default config
        registerConfig(Config("config"))
    }

    fun registerConfig(settings: Config) {
        val file = File(plugin.dataFolder, "${settings.name}.yml")

        if (!file.exists() && settings.createIfNotExists) {
            file.parentFile.mkdirs()
            if (settings.copyDefaults) {
                plugin.saveResource("${settings.name}.yml", false)
            } else {
                file.createNewFile()
            }
        }

        val config = if (settings.name == "config") {
            plugin.config
        } else {
            YamlConfiguration.loadConfiguration(file)
        }

        configs[settings.name] = config
        configFiles[settings.name] = file
        configSettings[settings.name] = settings

        if (settings.autoSave) {
            setupAutoSave(settings)
        }
    }

    private fun setupAutoSave(settings: Config) {
        autoSaveTasks[settings.name]?.let { taskId ->
            plugin.server.scheduler.cancelTask(taskId)
        }

        val taskId = if (settings.useAsyncSaving) {
            Async.asyncTimer(plugin, 0, settings.saveInterval * 20) {
                saveConfig(settings.name)
            }.taskId
        } else {
            Async.timer(plugin, 0, settings.saveInterval * 20) {
                saveConfig(settings.name, async = false)
            }.taskId
        }

        autoSaveTasks[settings.name] = taskId
    }

    fun getConfig(name: String = "config"): FileConfiguration {
        return configs[name] ?: throw IllegalArgumentException("Config $name not found! Register it first.")
    }

    fun saveConfig(name: String = "config", async: Boolean = true) {
        val config = configs[name] ?: throw IllegalArgumentException("Config $name not found!")
        val file = configFiles[name] ?: throw IllegalArgumentException("Config file $name not found!")
        val settings = configSettings[name] ?: throw IllegalArgumentException("Config settings for $name not found!")

        val saveTask = {
            try {
                config.save(file)
                plugin.logger.info("Saved config $name")
            } catch (e: IOException) {
                plugin.logger.severe("Could not save config $name: ${e.message}")
            }
        }

        if (async && settings.useAsyncSaving) {
            Async.async(plugin) { saveTask() }
        } else {
            saveTask()
        }
    }

    fun reloadConfig(name: String = "config") {
        if (name == "config") {
            plugin.reloadConfig()
            configs["config"] = plugin.config
            return
        }

        val file = configFiles[name] ?: throw IllegalArgumentException("Config file $name not found!")
        val newConfig = YamlConfiguration.loadConfiguration(file)

        // Preserve comments and structure from the original file
        val defaultsStream = plugin.getResource("${name}.yml")
        if (defaultsStream != null) {
            newConfig.setDefaults(YamlConfiguration.loadConfiguration(defaultsStream.reader()))
        }

        configs[name] = newConfig
    }

    fun getString(path: String, name: String = "config"): String? {
        return getConfig(name).getString(path)
    }

    fun getInt(path: String, name: String = "config"): Int {
        return getConfig(name).getInt(path)
    }

    fun getBoolean(path: String, name: String = "config"): Boolean {
        return getConfig(name).getBoolean(path)
    }

    fun getStringList(path: String, name: String = "config"): List<String> {
        return getConfig(name).getStringList(path)
    }

    fun set(path: String, value: Any?, name: String = "config", autoSave: Boolean = true) {
        getConfig(name).set(path, value)
        if (autoSave) {
            saveConfig(name)
        }
    }

    fun reloadAllConfigs() {
        configs.keys.forEach { reloadConfig(it) }
    }

    fun saveAllConfigs(async: Boolean = true) {
        configs.keys.forEach { saveConfig(it, async) }
    }

    fun shutdown() {
        autoSaveTasks.values.forEach { taskId ->
            plugin.server.scheduler.cancelTask(taskId)
        }
        saveAllConfigs(async = false)
    }

    fun getOrCreateSection(name: String, path: String): ConfigurationSection {
        val config = getConfig(name)
        return config.getConfigurationSection(path) ?: config.createSection(path)
    }

    fun getKeys(name: String, path: String, deep: Boolean = false): Set<String> {
        return getConfig(name).getConfigurationSection(path)?.getKeys(deep) ?: emptySet()
    }

    fun exists(name: String, path: String): Boolean {
        return getConfig(name).contains(path)
    }

    fun copyDefaults(name: String) {
        val config = getConfig(name)
        val defaults = YamlConfiguration.loadConfiguration(
            plugin.getResource("${name}.yml")?.reader() ?: return
        )
        config.setDefaults(defaults)
        config.options().copyDefaults(true)
        saveConfig(name)
    }
}