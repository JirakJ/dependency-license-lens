package com.jakubjirak.licenselens.model

/**
 * Represents a project dependency with coordinates.
 */
data class Dependency(
    val group: String,
    val artifact: String,
    val version: String,
    val scope: String = "implementation",
)
