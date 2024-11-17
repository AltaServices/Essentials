package dev.alta.essentials.ranks

data class Rank(
    val name: String,
    val weight: Int,
    val prefix: String?,
    val permissions: List<String>,
    val parents: List<String>
)
