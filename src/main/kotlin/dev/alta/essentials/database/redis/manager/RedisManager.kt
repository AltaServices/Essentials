package dev.alta.essentials.database.redis.manager

import Redis
import dev.alta.essentials.async.Async
import dev.alta.essentials.database.redis.connection.RedisConnection
import org.bukkit.plugin.Plugin
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.lettuce.core.pubsub.RedisPubSubListener

class RedisManager(
    private val plugin: Plugin,
    private val connection: RedisConnection
) {
    companion object {
        private val connections = ConcurrentHashMap<String, RedisConnection>()

        fun createConnection(plugin: Plugin, config: Redis): RedisConnection {
            return connections.computeIfAbsent(plugin.name) {
                RedisConnection(plugin, config)
            }
        }

        fun getConnection(plugin: Plugin): RedisConnection? {
            return connections[plugin.name]
        }

        fun closeConnection(plugin: Plugin) {
            connections.remove(plugin.name)?.close()
        }
    }

    fun <T> asyncOperation(block: () -> T): CompletableFuture<T> {
        return Async.asyncCallback(plugin) {
            block()
        }
    }

    fun set(key: String, value: String): CompletableFuture<String> = asyncOperation {
        connection.sync().set(key, value)
    }

    fun get(key: String): CompletableFuture<String?> = asyncOperation {
        connection.sync().get(key)
    }

    fun setEx(key: String, seconds: Long, value: String): CompletableFuture<String> = asyncOperation {
        connection.sync().setex(key, seconds, value)
    }

    fun del(key: String): CompletableFuture<Long> = asyncOperation {
        connection.sync().del(key)
    }

    fun exists(key: String): CompletableFuture<Boolean> = asyncOperation {
        connection.sync().exists(key) > 0
    }

    fun publish(channel: String, message: String): CompletableFuture<Long> = asyncOperation {
        connection.sync().publish(channel, message)
    }

    fun subscribe(channel: String, callback: (String, String) -> Unit) {
        val pubSubConnection = connection.pubSub()
        pubSubConnection.addListener(object : RedisPubSubListener<String, String> {
            override fun message(channel: String, message: String) {
                callback(channel, message)
            }
            
            override fun message(pattern: String, channel: String, message: String) {
                // Not used for direct channel subscriptions
            }
            
            override fun subscribed(channel: String, count: Long) {
                plugin.logger.info("Subscribed to Redis channel: $channel")
            }
            
            override fun unsubscribed(channel: String, count: Long) {
                plugin.logger.info("Unsubscribed from Redis channel: $channel")
            }
            
            override fun psubscribed(pattern: String, count: Long) {
                // Not used for direct channel subscriptions
            }
            
            override fun punsubscribed(pattern: String, count: Long) {
                // Not used for direct channel subscriptions
            }
        })
        
        pubSubConnection.sync().subscribe(channel)
    }
} 