package com.jakubjirak.licenselens.ui

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Opens the License Lens tool window and runs a dependency license scan.
 */
class ScanDependenciesAction : AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID) ?: return
        toolWindow.show {
            toolWindow.contentManager.contents
                .map { it.component }
                .filterIsInstance<LicenseLensPanel>()
                .firstOrNull()
                ?.scanDependencies()
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    private companion object {
        const val TOOL_WINDOW_ID = "License Lens"
    }
}
