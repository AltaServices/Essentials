package dev.alta.essentials.database.redis.connection

import Redis
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import org.bukkit.plugin.Plugin
import java.time.Duration

class RedisConnection(
    private val plugin: Plugin,
    private val config: Redis
) : AutoCloseable {
    private val client: RedisClient
    private val connection: StatefulRedisConnection<String, String>
    private var pubSubConnection: StatefulRedisPubSubConnection<String, String>? = null

    init {
        val redisUri = RedisURI.builder()
            .withHost(config.host)
            .withPort(config.port)
            .withDatabase(config.database)
            .withTimeout(Duration.ofMillis(config.timeout.toLong()))
            .apply {
                if (config.password != null) {
                    withPassword(config.password.toCharArray())
                }
                if (config.ssl) {
                    withSsl(true)
                }
            }
            .build()

        client = RedisClient.create(redisUri)
        connection = client.connect()
    }

    fun sync(): RedisCommands<String, String> = connection.sync()
    
    fun async(): RedisAsyncCommands<String, String> = connection.async()

    fun pubSub(): StatefulRedisPubSubConnection<String, String> {
        if (pubSubConnection == null) {
            pubSubConnection = client.connectPubSub()
        }
        return pubSubConnection!!
    }

    override fun close() {
        pubSubConnection?.close()
        connection.close()
        client.shutdown()
    }
} 