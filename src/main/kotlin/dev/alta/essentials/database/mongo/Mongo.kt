package dev.alta.essentials.database.mongo

data class Mongo(
    val connectionString: String,
    val database: String,
    val collections: Map<String, String> = emptyMap(),
    val username: String? = null,
    val password: String? = null,
    val authDatabase: String? = null,
    val options: Map<String, String> = emptyMap()
) 