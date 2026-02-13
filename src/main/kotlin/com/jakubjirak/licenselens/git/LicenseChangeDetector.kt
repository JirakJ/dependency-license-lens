package com.jakubjirak.licenselens.git

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project

/**
 * Detects when dependency licenses change between commits.
 * Monitors lockfile/build file changes in git history.
 */
class LicenseChangeDetector {

    /**
     * Represents a detected license change for a dependency.
     */
    data class LicenseChange(
        val dependency: String,
        val oldLicense: String?,
        val newLicense: String?,
        val commitHash: String,
        val date: Long,
        val author: String,
    )

    /**
     * Type of license change detected.
     */
    enum class ChangeType {
        DEPENDENCY_ADDED,
        DEPENDENCY_REMOVED,
        VERSION_CHANGED,
        LICENSE_CHANGED,
    }

    /**
     * Git commit context for a license change detection.
     */
    data class CommitContext(
        val commitHash: String,
        val date: Long,
        val author: String,
    )

    /**
     * Detects license changes from git diff between two commits.
     */
    @Suppress("UNUSED_PARAMETER")
    fun detectChanges(
        project: Project,
        oldDependencies: Map<String, String?>,
        newDependencies: Map<String, String?>,
        context: CommitContext,
    ): List<LicenseChange> {
        val changes = mutableListOf<LicenseChange>()

        val addedDeps = newDependencies.keys - oldDependencies.keys
        for (dep in addedDeps) {
            changes.add(
                LicenseChange(
                    dependency = dep,
                    oldLicense = null,
                    newLicense = newDependencies[dep],
                    commitHash = context.commitHash,
                    date = context.date,
                    author = context.author,
                ),
            )
        }

        val removedDeps = oldDependencies.keys - newDependencies.keys
        for (dep in removedDeps) {
            changes.add(
                LicenseChange(
                    dependency = dep,
                    oldLicense = oldDependencies[dep],
                    newLicense = null,
                    commitHash = context.commitHash,
                    date = context.date,
                    author = context.author,
                ),
            )
        }

        val commonDeps = oldDependencies.keys.intersect(newDependencies.keys)
        for (dep in commonDeps) {
            val oldLicense = oldDependencies[dep]
            val newLicense = newDependencies[dep]
            if (oldLicense != newLicense) {
                changes.add(
                    LicenseChange(
                        dependency = dep,
                        oldLicense = oldLicense,
                        newLicense = newLicense,
                        commitHash = context.commitHash,
                        date = context.date,
                        author = context.author,
                    ),
                )
            }
        }

        LOG.info("Detected ${changes.size} license changes in commit ${context.commitHash}")
        return changes
    }

    /**
     * Classifies the type of license change.
     */
    fun classifyChange(change: LicenseChange): ChangeType {
        return when {
            change.oldLicense == null && change.newLicense != null -> ChangeType.DEPENDENCY_ADDED
            change.oldLicense != null && change.newLicense == null -> ChangeType.DEPENDENCY_REMOVED
            change.oldLicense != change.newLicense -> ChangeType.LICENSE_CHANGED
            else -> ChangeType.VERSION_CHANGED
        }
    }

    /**
     * Checks if a license change represents a risk escalation.
     */
    fun isRiskEscalation(change: LicenseChange, blockedLicenses: Set<String>): Boolean {
        val newLicense = change.newLicense ?: return false
        val oldBlocked = change.oldLicense?.let { it in blockedLicenses } ?: false
        val newBlocked = newLicense in blockedLicenses
        return !oldBlocked && newBlocked
    }

    /**
     * Files monitored for dependency changes.
     */
    fun getMonitoredFiles(): List<String> = MONITORED_FILES

    companion object {
        private val LOG = Logger.getInstance(LicenseChangeDetector::class.java)

        val MONITORED_FILES = listOf(
            "build.gradle.kts",
            "build.gradle",
            "pom.xml",
            "package-lock.json",
            "yarn.lock",
            "requirements.txt",
            "go.sum",
        )
    }
}
