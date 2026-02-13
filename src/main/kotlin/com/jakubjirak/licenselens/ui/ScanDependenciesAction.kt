package com.jakubjirak.licenselens.ui

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Action to trigger a dependency license scan and show the tool window.
 */
class ScanDependenciesAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("License Lens")
        if (toolWindow != null) {
            toolWindow.show()
            LOG.info("License Lens tool window opened")
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    companion object {
        private val LOG = Logger.getInstance(ScanDependenciesAction::class.java)
    }
}
