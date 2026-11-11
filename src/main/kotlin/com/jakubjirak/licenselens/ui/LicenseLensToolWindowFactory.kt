package com.jakubjirak.licenselens.ui

import com.jakubjirak.licenselens.model.ComplianceReport
import com.jakubjirak.licenselens.scan.LicenseScanner
import com.jakubjirak.licenselens.settings.LicenseLensSettings
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
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
    private val scanner = LicenseScanner()

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

    /** Scans the build files in the project root and refreshes the table. */
    fun scanDependencies() {
        val basePath = project.basePath ?: return
        val baseDir = File(basePath)
        val files = LicenseScanner.BUILD_FILE_NAMES
            .map { baseDir.resolve(it) }
            .filter { it.isFile }
            .map { it.name to it.readText() }

        val report = scanner.scan(files, LicenseLensSettings.getInstance().toPolicy())
        updateTable(report)
        LOG.info("License scan completed: ${report.summary.totalDependencies} dependencies, ${report.summary.issueCount} issues")
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
