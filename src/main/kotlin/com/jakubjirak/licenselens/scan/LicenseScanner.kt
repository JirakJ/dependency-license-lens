package com.jakubjirak.licenselens.scan

import com.jakubjirak.licenselens.model.ComplianceReport
import com.jakubjirak.licenselens.model.ComplianceSummary
import com.jakubjirak.licenselens.model.Dependency
import com.jakubjirak.licenselens.model.LicenseCategory
import com.jakubjirak.licenselens.model.LicenseRisk
import com.jakubjirak.licenselens.model.ResolvedDependency
import com.jakubjirak.licenselens.parser.DependencyParser
import com.jakubjirak.licenselens.parser.GradleDependencyParser
import com.jakubjirak.licenselens.parser.MavenDependencyParser
import com.jakubjirak.licenselens.parser.NpmDependencyParser
import com.jakubjirak.licenselens.policy.LicensePolicy
import com.jakubjirak.licenselens.resolver.LicenseResolver

/**
 * IDE-independent scan pipeline: build file contents -> parsed dependencies -> resolved licenses -> report.
 */
class LicenseScanner(
    private val parsers: List<DependencyParser> = DEFAULT_PARSERS,
    private val resolver: LicenseResolver = LicenseResolver(),
) {

    /** True when one of the parsers understands a file with this name. */
    fun isSupported(fileName: String): Boolean = parsers.any { it.supports(fileName) }

    /** Parses every supported file; unsupported file names are ignored. Keys are file names (not paths). */
    fun parseAll(filesByName: List<Pair<String, String>>): List<Dependency> =
        filesByName.flatMap { (name, content) ->
            parsers.firstOrNull { it.supports(name) }?.parse(content).orEmpty()
        }

    /** Full scan producing a compliance report. */
    fun scan(filesByName: List<Pair<String, String>>, policy: LicensePolicy): ComplianceReport {
        val resolved = resolver.resolveAll(parseAll(filesByName), policy)
        return ComplianceReport(
            resolvedDependencies = resolved,
            issues = policy.evaluate(resolved),
            summary = summarize(resolved),
        )
    }

    companion object {
        /** Build files the scanner looks for in the project root. */
        val BUILD_FILE_NAMES = listOf("build.gradle", "build.gradle.kts", "pom.xml", "package.json")

        private val DEFAULT_PARSERS: List<DependencyParser> = listOf(
            GradleDependencyParser(),
            MavenDependencyParser(),
            NpmDependencyParser(),
        )

        fun summarize(resolved: List<ResolvedDependency>): ComplianceSummary = ComplianceSummary(
            totalDependencies = resolved.size,
            permissiveCount = resolved.count { it.license?.category == LicenseCategory.PERMISSIVE },
            weakCopyleftCount = resolved.count { it.license?.category == LicenseCategory.WEAK_COPYLEFT },
            strongCopyleftCount = resolved.count { it.license?.category == LicenseCategory.STRONG_COPYLEFT },
            unknownCount = resolved.count { it.license == null || it.license.category == LicenseCategory.UNKNOWN },
            issueCount = resolved.count { it.risk == LicenseRisk.WARNING || it.risk == LicenseRisk.BLOCKED },
        )
    }
}
