package dev.alta.essentials.async

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Async {
    private val plugins = ConcurrentHashMap<String, Plugin>()

    fun registerPlugin(plugin: Plugin) {
        plugins[plugin.name] = plugin
    }

    fun unregisterPlugin(plugin: Plugin) {
        plugins.remove(plugin.name)
    }

    fun getPlugin(pluginName: String): Plugin {
        return plugins[pluginName] ?: throw IllegalStateException("Plugin $pluginName not registered with AsyncUtils!")
    }

    fun async(plugin: Plugin, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
    }

    fun sync(plugin: Plugin, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTask(plugin, task)
    }

    fun <T> asyncCallback(plugin: Plugin, task: () -> T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        async(plugin) {
            try {
                val result = task()
                sync(plugin) { future.complete(result) }
            } catch (e: Exception) {
                sync(plugin) { future.completeExceptionally(e) }
            }
        }
        return future
    }

    fun later(plugin: Plugin, delay: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delay)
    }

    fun asyncLater(plugin: Plugin, delay: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay)
    }

    fun timer(plugin: Plugin, delay: Long, period: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period)
    }

    fun asyncTimer(plugin: Plugin, delay: Long, period: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period)
    }

    suspend fun <T> awaitAsync(plugin: Plugin, task: () -> T): T = suspendCoroutine { continuation ->
        async(plugin) {
            try {
                val result = task()
                sync(plugin) { continuation.resume(result) }
            } catch (e: Exception) {
                sync(plugin) { continuation.resumeWithException(e) }
            }
        }
    }
}