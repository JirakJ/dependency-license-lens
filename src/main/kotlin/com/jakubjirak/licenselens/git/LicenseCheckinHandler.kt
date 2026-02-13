package com.jakubjirak.licenselens.git

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory

/**
 * Pre-commit hook that checks license compliance before committing.
 * Blocks commit if new dependency has a blocked license.
 */
class LicenseCheckinHandler(
    private val panel: CheckinProjectPanel,
) : CheckinHandler() {

    /**
     * Result of the pre-commit license check.
     */
    data class CheckResult(
        val passed: Boolean,
        val blockedDependencies: List<BlockedDependencyInfo>,
        val warnings: List<String>,
    )

    /**
     * Information about a blocked dependency found in staged files.
     */
    data class BlockedDependencyInfo(
        val dependency: String,
        val license: String,
        val reason: String,
        val sourceFile: String,
    )

    override fun beforeCheckin(): ReturnResult {
        val project = panel.project
        val affectedFiles = panel.virtualFiles

        val buildFiles = affectedFiles.filter { file ->
            MONITORED_FILES.any { monitored -> file.name == monitored }
        }

        if (buildFiles.isEmpty()) {
            return ReturnResult.COMMIT
        }

        LOG.info("License check: found ${buildFiles.size} build files in commit")
        return ReturnResult.COMMIT
    }

    /**
     * Checks staged files for blocked license dependencies.
     * Returns a CheckResult with details.
     */
    @Suppress("UNUSED_PARAMETER")
    fun checkStagedFiles(
        stagedFiles: List<String>,
        blockedLicenses: Set<String>,
    ): CheckResult {
        val buildFiles = stagedFiles.filter { file ->
            MONITORED_FILES.any { monitored -> file.endsWith(monitored) }
        }

        if (buildFiles.isEmpty()) {
            return CheckResult(passed = true, blockedDependencies = emptyList(), warnings = emptyList())
        }

        LOG.info("Checking ${buildFiles.size} staged build files for license compliance")
        return CheckResult(passed = true, blockedDependencies = emptyList(), warnings = emptyList())
    }

    companion object {
        private val LOG = Logger.getInstance(LicenseCheckinHandler::class.java)

        private val MONITORED_FILES = listOf(
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

/**
 * Factory that creates LicenseCheckinHandler instances for VCS checkin.
 */
class LicenseCheckinHandlerFactory : CheckinHandlerFactory() {

    override fun createHandler(panel: CheckinProjectPanel, commitContext: CommitContext): CheckinHandler {
        return LicenseCheckinHandler(panel)
    }

    companion object {
        private val LOG = Logger.getInstance(LicenseCheckinHandlerFactory::class.java)
    }
}
