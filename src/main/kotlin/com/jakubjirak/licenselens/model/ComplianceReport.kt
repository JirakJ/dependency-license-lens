package com.jakubjirak.licenselens.model

/**
 * Compliance report summarizing all dependency licenses and issues.
 */
data class ComplianceReport(
    val resolvedDependencies: List<ResolvedDependency>,
    val issues: List<ComplianceIssue>,
    val summary: ComplianceSummary,
)

/**
 * A single compliance issue found during scanning.
 */
data class ComplianceIssue(
    val dependency: Dependency,
    val license: LicenseInfo?,
    val risk: LicenseRisk,
    val message: String,
)

/**
 * Summary statistics for a compliance report.
 */
data class ComplianceSummary(
    val totalDependencies: Int,
    val permissiveCount: Int,
    val weakCopyleftCount: Int,
    val strongCopyleftCount: Int,
    val unknownCount: Int,
    val issueCount: Int,
)
