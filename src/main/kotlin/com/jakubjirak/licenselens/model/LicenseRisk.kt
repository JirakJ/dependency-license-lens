package com.jakubjirak.licenselens.model

/**
 * Risk level for a dependency license in context of a project.
 */
enum class LicenseRisk(val displayName: String, val icon: String) {
    SAFE("Safe", "🟢"),
    CAUTION("Caution", "🟡"),
    WARNING("Warning", "🟠"),
    BLOCKED("Blocked", "🔴"),
}
