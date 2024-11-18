package dev.alta.essentials.async

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Async {
    private var plugin: Plugin? = null

    fun initialize(plugin: Plugin) {
        this.plugin = plugin
    }

    private fun getPlugin(): Plugin {
        return plugin ?: throw IllegalStateException("AsyncUtils not initialized! Call initialize() first")
    }

    fun async(task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), task)
    }

    fun sync(task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTask(getPlugin(), task)
    }

    fun <T> asyncCallback(task: () -> T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        async {
            try {
                val result = task()
                sync { future.complete(result) }
            } catch (e: Exception) {
                sync { future.completeExceptionally(e) }
            }
        }
        return future
    }

    fun later(delay: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskLater(getPlugin(), task, delay)
    }

    fun asyncLater(delay: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), task, delay)
    }

    fun timer(delay: Long, period: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskTimer(getPlugin(), task, delay, period)
    }

    fun asyncTimer(delay: Long, period: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), task, delay, period)
    }

    suspend fun <T> awaitAsync(task: () -> T): T = suspendCoroutine { continuation ->
        async {
            try {
                val result = task()
                sync { continuation.resume(result) }
            } catch (e: Exception) {
                sync { continuation.resumeWithException(e) }
            }
        }
    }
}