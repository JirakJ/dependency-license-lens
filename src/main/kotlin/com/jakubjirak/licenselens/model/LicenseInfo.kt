package com.jakubjirak.licenselens.model

/**
 * License information resolved for a dependency.
 */
data class LicenseInfo(
    val spdxId: String,
    val name: String,
    val url: String,
    val category: LicenseCategory,
)
