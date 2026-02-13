package com.jakubjirak.licenselens.policy

import com.jakubjirak.licenselens.model.ComplianceIssue
import com.jakubjirak.licenselens.model.LicenseRisk
import com.jakubjirak.licenselens.model.ResolvedDependency
import com.intellij.openapi.diagnostic.Logger

/**
 * Configurable license policy for determining which licenses are acceptable.
 */
class LicensePolicy(
    val isCommercialProject: Boolean = true,
    private val blockedLicenses: Set<String> = emptySet(),
    val isPolicyEnabled: Boolean = false,
) {

    /**
     * Check if a license SPDX ID is blocked by the policy.
     */
    fun isBlocked(spdxId: String): Boolean {
        if (!isPolicyEnabled) return false
        return spdxId in blockedLicenses
    }

    /**
     * Evaluate resolved dependencies and find compliance issues.
     */
    fun evaluate(resolvedDependencies: List<ResolvedDependency>): List<ComplianceIssue> {
        val issues = mutableListOf<ComplianceIssue>()

        for (resolved in resolvedDependencies) {
            when (resolved.risk) {
                LicenseRisk.BLOCKED -> {
                    issues.add(
                        ComplianceIssue(
                            dependency = resolved.dependency,
                            license = resolved.license,
                            risk = LicenseRisk.BLOCKED,
                            message = "License ${resolved.license?.spdxId ?: "unknown"} is blocked by policy",
                        )
                    )
                }
                LicenseRisk.WARNING -> {
                    issues.add(
                        ComplianceIssue(
                            dependency = resolved.dependency,
                            license = resolved.license,
                            risk = LicenseRisk.WARNING,
                            message = "License ${resolved.license?.spdxId ?: "unknown"} may not be compatible with project type",
                        )
                    )
                }
                else -> {
                    if (resolved.license == null) {
                        issues.add(
                            ComplianceIssue(
                                dependency = resolved.dependency,
                                license = null,
                                risk = LicenseRisk.CAUTION,
                                message = "License could not be resolved for ${resolved.dependency.group}:${resolved.dependency.artifact}",
                            )
                        )
                    }
                }
            }
        }

        LOG.debug("Policy evaluation found ${issues.size} issues")
        return issues
    }

    companion object {
        private val LOG = Logger.getInstance(LicensePolicy::class.java)

        val DEFAULT = LicensePolicy(
            isCommercialProject = true,
            blockedLicenses = setOf("GPL-2.0", "GPL-3.0", "AGPL-3.0"),
            isPolicyEnabled = false,
        )
    }
}
