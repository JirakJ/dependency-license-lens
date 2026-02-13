package com.jakubjirak.licenselens.git

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project

/**
 * Generates compliance report for dependencies changed in a branch.
 * Compares current branch deps vs base branch.
 */
class PrLicenseAuditor {

    /**
     * License audit report for a pull request.
     */
    data class PrLicenseReport(
        val addedDeps: List<DependencyLicenseInfo>,
        val removedDeps: List<DependencyLicenseInfo>,
        val changedLicenses: List<LicenseChangeInfo>,
        val violations: List<String>,
        val compliant: Boolean,
    )

    /**
     * Dependency with its license information for audit purposes.
     */
    data class DependencyLicenseInfo(
        val dependency: String,
        val license: String?,
    )

    /**
     * A specific license change within a PR.
     */
    data class LicenseChangeInfo(
        val dependency: String,
        val previousLicense: String?,
        val newLicense: String?,
    )

    /**
     * Audit status for the overall report.
     */
    enum class AuditStatus {
        PASS,
        WARNING,
        FAIL,
    }

    /**
     * Generates a license audit report comparing branch dependencies with base.
     */
    @Suppress("UNUSED_PARAMETER")
    fun audit(
        project: Project,
        baseDependencies: Map<String, String?>,
        branchDependencies: Map<String, String?>,
        blockedLicenses: Set<String>,
    ): PrLicenseReport {
        val addedDeps = mutableListOf<DependencyLicenseInfo>()
        val removedDeps = mutableListOf<DependencyLicenseInfo>()
        val changedLicenses = mutableListOf<LicenseChangeInfo>()
        val violations = mutableListOf<String>()

        val added = branchDependencies.keys - baseDependencies.keys
        for (dep in added) {
            val license = branchDependencies[dep]
            addedDeps.add(DependencyLicenseInfo(dep, license))
            if (license != null && license in blockedLicenses) {
                violations.add("Added dependency '$dep' has blocked license: $license")
            }
        }

        val removed = baseDependencies.keys - branchDependencies.keys
        for (dep in removed) {
            removedDeps.add(DependencyLicenseInfo(dep, baseDependencies[dep]))
        }

        val common = baseDependencies.keys.intersect(branchDependencies.keys)
        for (dep in common) {
            val oldLicense = baseDependencies[dep]
            val newLicense = branchDependencies[dep]
            if (oldLicense != newLicense) {
                changedLicenses.add(LicenseChangeInfo(dep, oldLicense, newLicense))
                if (newLicense != null && newLicense in blockedLicenses) {
                    violations.add("Dependency '$dep' license changed to blocked: $newLicense")
                }
            }
        }

        val compliant = violations.isEmpty()

        LOG.info(
            "PR audit: ${addedDeps.size} added, ${removedDeps.size} removed, " +
                "${changedLicenses.size} changed, ${violations.size} violations",
        )

        return PrLicenseReport(
            addedDeps = addedDeps,
            removedDeps = removedDeps,
            changedLicenses = changedLicenses,
            violations = violations,
            compliant = compliant,
        )
    }

    /**
     * Determines the audit status from a report.
     */
    fun getAuditStatus(report: PrLicenseReport): AuditStatus {
        return when {
            report.violations.isNotEmpty() -> AuditStatus.FAIL
            report.changedLicenses.isNotEmpty() -> AuditStatus.WARNING
            else -> AuditStatus.PASS
        }
    }

    /**
     * Generates a Markdown summary of the audit report.
     */
    fun generateMarkdownReport(report: PrLicenseReport): String {
        val sb = StringBuilder()
        sb.appendLine("## License Audit Report")
        sb.appendLine()

        val status = getAuditStatus(report)
        val statusIcon = when (status) {
            AuditStatus.PASS -> "✅"
            AuditStatus.WARNING -> "⚠️"
            AuditStatus.FAIL -> "❌"
        }
        sb.appendLine("**Status:** $statusIcon ${status.name}")
        sb.appendLine()

        if (report.addedDeps.isNotEmpty()) {
            sb.appendLine("### Added Dependencies")
            for (dep in report.addedDeps) {
                sb.appendLine("- ${dep.dependency} (${dep.license ?: "unknown"})")
            }
            sb.appendLine()
        }

        if (report.removedDeps.isNotEmpty()) {
            sb.appendLine("### Removed Dependencies")
            for (dep in report.removedDeps) {
                sb.appendLine("- ${dep.dependency}")
            }
            sb.appendLine()
        }

        if (report.violations.isNotEmpty()) {
            sb.appendLine("### Violations")
            for (violation in report.violations) {
                sb.appendLine("- ❌ $violation")
            }
            sb.appendLine()
        }

        return sb.toString()
    }

    companion object {
        private val LOG = Logger.getInstance(PrLicenseAuditor::class.java)
    }
}
