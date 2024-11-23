data class Redis(
    val host: String = "localhost",
    val port: Int = 6379,
    val password: String? = null,
    val database: Int = 0,
    val ssl: Boolean = false,
    val timeout: Int = 2000,
    val poolSize: Int = 8
) 