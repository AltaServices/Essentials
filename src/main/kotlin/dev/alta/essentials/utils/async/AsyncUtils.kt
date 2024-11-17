package dev.alta.essentials.utils.async

import dev.alta.essentials.Essentials
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object AsyncUtils {
    private val plugin = Essentials.instance


    fun async(task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task)
    }


    fun sync(task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTask(plugin, task)
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
        return Bukkit.getScheduler().runTaskLater(plugin, task, delay)
    }


    fun asyncLater(delay: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay)
    }


    fun timer(delay: Long, period: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period)
    }


    fun asyncTimer(delay: Long, period: Long, task: () -> Unit): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period)
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