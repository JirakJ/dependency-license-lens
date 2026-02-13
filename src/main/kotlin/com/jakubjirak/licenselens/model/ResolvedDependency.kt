package com.jakubjirak.licenselens.model

/**
 * A resolved dependency with its license information.
 */
data class ResolvedDependency(
    val dependency: Dependency,
    val license: LicenseInfo?,
    val risk: LicenseRisk,
)
