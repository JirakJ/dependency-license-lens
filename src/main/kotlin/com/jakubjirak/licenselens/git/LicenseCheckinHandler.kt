package com.jakubjirak.licenselens.git

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory
import com.intellij.openapi.vfs.VfsUtilCore
import com.jakubjirak.licenselens.model.LicenseRisk
import com.jakubjirak.licenselens.policy.LicensePolicy
import com.jakubjirak.licenselens.scan.LicenseScanner
import com.jakubjirak.licenselens.settings.LicenseLensSettings
import java.io.IOException

/**
 * Pre-commit check: when the commit touches a supported build file and the license policy is enabled,
 * dependencies whose license is blocked by the policy are reported and the user is asked whether to commit anyway.
 */
class LicenseCheckinHandler(
    private val panel: CheckinProjectPanel,
    private val checker: StagedLicenseChecker = StagedLicenseChecker(),
) : CheckinHandler() {

    override fun beforeCheckin(): ReturnResult {
        val files = panel.virtualFiles
            .filter { !it.isDirectory && checker.isMonitored(it.name) }
            .mapNotNull { file ->
                try {
                    file.name to VfsUtilCore.loadText(file)
                } catch (e: IOException) {
                    LOG.warn("Cannot read ${file.path} for license check", e)
                    null
                }
            }
        if (files.isEmpty()) return ReturnResult.COMMIT

        val result = checker.check(files, LicenseLensSettings.getInstance().toPolicy())
        if (result.passed) return ReturnResult.COMMIT

        val answer = Messages.showYesNoDialog(
            panel.project,
            result.describe(),
            "License Policy Violation",
            "Commit Anyway",
            "Cancel",
            Messages.getWarningIcon(),
        )
        return if (answer == Messages.YES) ReturnResult.COMMIT else ReturnResult.CANCEL
    }

    companion object {
        private val LOG = Logger.getInstance(LicenseCheckinHandler::class.java)
    }
}

/**
 * IDE-independent part of the pre-commit check.
 */
class StagedLicenseChecker(private val scanner: LicenseScanner = LicenseScanner()) {

    /** Result of the pre-commit license check. */
    data class CheckResult(
        val passed: Boolean,
        val blockedDependencies: List<BlockedDependencyInfo>,
    ) {
        fun describe(): String = buildString {
            appendLine("The following dependencies use licenses blocked by your License Lens policy:")
            appendLine()
            blockedDependencies.forEach { appendLine("• ${it.dependency} — ${it.license} (${it.sourceFile})") }
            appendLine()
            append("Commit anyway?")
        }
    }

    /** A dependency with a blocked license found in a committed build file. */
    data class BlockedDependencyInfo(
        val dependency: String,
        val license: String,
        val sourceFile: String,
    )

    fun isMonitored(fileName: String): Boolean = scanner.isSupported(fileName)

    /**
     * Checks the given build files (name to content). Unsupported files are ignored.
     */
    fun check(files: List<Pair<String, String>>, policy: LicensePolicy): CheckResult {
        if (!policy.isPolicyEnabled) return CheckResult(passed = true, blockedDependencies = emptyList())

        val blocked = files.flatMap { file ->
            scanner.scan(listOf(file), policy).resolvedDependencies
                .filter { it.risk == LicenseRisk.BLOCKED }
                .map {
                    BlockedDependencyInfo(
                        dependency = "${it.dependency.group}:${it.dependency.artifact}",
                        license = it.license?.spdxId ?: "unknown",
                        sourceFile = file.first,
                    )
                }
        }.distinct()

        return CheckResult(passed = blocked.isEmpty(), blockedDependencies = blocked)
    }
}

/**
 * Factory that creates LicenseCheckinHandler instances for VCS checkin.
 */
class LicenseCheckinHandlerFactory : CheckinHandlerFactory() {

    override fun createHandler(panel: CheckinProjectPanel, commitContext: CommitContext): CheckinHandler =
        LicenseCheckinHandler(panel)
}
