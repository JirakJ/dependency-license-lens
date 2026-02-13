package com.jakubjirak.licenselens.model

/**
 * License category classification based on copyleft strength.
 */
enum class LicenseCategory(val displayName: String) {
    PERMISSIVE("Permissive"),
    WEAK_COPYLEFT("Weak Copyleft"),
    STRONG_COPYLEFT("Strong Copyleft"),
    PROPRIETARY("Proprietary"),
    UNKNOWN("Unknown"),
}
