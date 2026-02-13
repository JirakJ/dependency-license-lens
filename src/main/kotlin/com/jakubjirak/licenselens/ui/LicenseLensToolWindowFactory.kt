package com.jakubjirak.licenselens.ui

import com.jakubjirak.licenselens.licensing.FreemiumGate
import com.jakubjirak.licenselens.model.ComplianceReport
import com.jakubjirak.licenselens.model.ComplianceSummary
import com.jakubjirak.licenselens.model.LicenseCategory
import com.jakubjirak.licenselens.model.LicenseRisk
import com.jakubjirak.licenselens.model.ResolvedDependency
import com.jakubjirak.licenselens.parser.DependencyParser
import com.jakubjirak.licenselens.parser.GradleDependencyParser
import com.jakubjirak.licenselens.parser.MavenDependencyParser
import com.jakubjirak.licenselens.parser.NpmDependencyParser
import com.jakubjirak.licenselens.policy.LicensePolicy
import com.jakubjirak.licenselens.resolver.LicenseResolver
import com.jakubjirak.licenselens.settings.LicenseLensSettings
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.io.File
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

/**
 * Tool window factory for the License Lens compliance dashboard.
 */
class LicenseLensToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = LicenseLensPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "Compliance", false)
        toolWindow.contentManager.addContent(content)
    }

    companion object {
        private val LOG = Logger.getInstance(LicenseLensToolWindowFactory::class.java)
    }
}

/**
 * Main panel for the License Lens tool window showing dependency compliance data.
 */
class LicenseLensPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val tableModel = object : DefaultTableModel(COLUMN_NAMES, 0) {
        override fun isCellEditable(row: Int, column: Int): Boolean = false
    }
    private val table = JBTable(tableModel)
    private val summaryLabel = JBLabel("No scan results. Click 'Scan' to analyze dependencies.")
    private val resolver = LicenseResolver()
    private val parsers: List<DependencyParser> = listOf(
        GradleDependencyParser(),
        MavenDependencyParser(),
        NpmDependencyParser(),
    )

    init {
        setupUI()
    }

    private fun setupUI() {
        val toolbar = JPanel().apply {
            val scanButton = JButton("Scan Dependencies").apply {
                addActionListener { scanDependencies() }
            }
            add(scanButton)
        }

        table.setDefaultRenderer(Any::class.java, RiskCellRenderer())

        add(toolbar, BorderLayout.NORTH)
        add(JBScrollPane(table), BorderLayout.CENTER)
        add(summaryLabel, BorderLayout.SOUTH)
    }

    private fun scanDependencies() {
        val basePath = project.basePath ?: return
        val baseDir = File(basePath)
        val settings = LicenseLensSettings.getInstance()
        val policy = settings.toPolicy()
        val allDependencies = mutableListOf<com.jakubjirak.licenselens.model.Dependency>()

        val buildFiles = listOf(
            "build.gradle", "build.gradle.kts", "pom.xml", "package.json"
        )

        for (fileName in buildFiles) {
            val file = baseDir.resolve(fileName)
            if (file.exists()) {
                val parser = parsers.find { it.supports(fileName) } ?: continue
                val deps = parser.parse(file.readText())
                allDependencies.addAll(deps)
            }
        }

        val freemiumGate = FreemiumGate.getInstance()
        val maxDeps = freemiumGate.getMaxFreeDependencies()
        val limited = if (allDependencies.size > maxDeps) allDependencies.take(maxDeps) else allDependencies

        val resolved = resolver.resolveAll(limited, policy)
        val issues = policy.evaluate(resolved)
        val report = ComplianceReport(
            resolvedDependencies = resolved,
            issues = issues,
            summary = buildSummary(resolved),
        )

        updateTable(report)
        LOG.info("License scan completed: ${report.summary.totalDependencies} dependencies, ${report.summary.issueCount} issues")
    }

    private fun buildSummary(resolved: List<ResolvedDependency>): ComplianceSummary {
        return ComplianceSummary(
            totalDependencies = resolved.size,
            permissiveCount = resolved.count { it.license?.category == LicenseCategory.PERMISSIVE },
            weakCopyleftCount = resolved.count { it.license?.category == LicenseCategory.WEAK_COPYLEFT },
            strongCopyleftCount = resolved.count { it.license?.category == LicenseCategory.STRONG_COPYLEFT },
            unknownCount = resolved.count { it.license == null || it.license.category == LicenseCategory.UNKNOWN },
            issueCount = resolved.count { it.risk == LicenseRisk.WARNING || it.risk == LicenseRisk.BLOCKED },
        )
    }

    private fun updateTable(report: ComplianceReport) {
        tableModel.rowCount = 0

        for (resolved in report.resolvedDependencies) {
            val dep = resolved.dependency
            tableModel.addRow(
                arrayOf<Any>(
                    "${dep.group}:${dep.artifact}",
                    dep.version,
                    resolved.license?.spdxId ?: "Unknown",
                    resolved.license?.category?.displayName ?: "Unknown",
                    "${resolved.risk.icon} ${resolved.risk.displayName}",
                )
            )
        }

        val s = report.summary
        summaryLabel.text = "Total: ${s.totalDependencies} | " +
            "Permissive: ${s.permissiveCount} | " +
            "Weak Copyleft: ${s.weakCopyleftCount} | " +
            "Strong Copyleft: ${s.strongCopyleftCount} | " +
            "Unknown: ${s.unknownCount} | " +
            "Issues: ${s.issueCount}"
    }

    companion object {
        private val LOG = Logger.getInstance(LicenseLensPanel::class.java)

        private val COLUMN_NAMES = arrayOf("Dependency", "Version", "License", "Category", "Risk")
    }
}

/**
 * Custom cell renderer that colors rows based on risk level.
 */
private class RiskCellRenderer : DefaultTableCellRenderer() {

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int,
    ): Component {
        val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)

        if (!isSelected) {
            val riskValue = table.model.getValueAt(row, RISK_COLUMN) as? String ?: ""
            component.foreground = when {
                riskValue.contains("Blocked") -> Color(0xE5, 0x39, 0x35)
                riskValue.contains("Warning") -> Color(0xFB, 0x8C, 0x00)
                riskValue.contains("Caution") -> Color(0xFF, 0xA7, 0x26)
                else -> table.foreground
            }
        }

        return component
    }

    companion object {
        private const val RISK_COLUMN = 4
    }
}
