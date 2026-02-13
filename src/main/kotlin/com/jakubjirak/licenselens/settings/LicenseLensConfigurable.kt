package com.jakubjirak.licenselens.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class LicenseLensConfigurable : Configurable {

    private var commercialCheckbox: JBCheckBox? = null
    private var policyCheckbox: JBCheckBox? = null
    private var blockedField: JBTextField? = null
    private var mainPanel: JPanel? = null

    override fun getDisplayName(): String = "Dependency License Lens"

    override fun createComponent(): JComponent {
        val settings = LicenseLensSettings.getInstance()

        commercialCheckbox = JBCheckBox("Commercial project (flag copyleft licenses)", settings.isCommercialProject)
        policyCheckbox = JBCheckBox("Enable license policy enforcement", settings.isPolicyEnabled)
        blockedField = JBTextField(settings.blockedLicenses).apply {
            emptyText.text = "GPL-2.0, GPL-3.0, AGPL-3.0"
        }

        mainPanel = FormBuilder.createFormBuilder()
            .addComponent(commercialCheckbox!!)
            .addComponent(policyCheckbox!!)
            .addLabeledComponent(JBLabel("Blocked licenses (comma-separated):"), blockedField!!)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        return mainPanel!!
    }

    override fun isModified(): Boolean {
        val settings = LicenseLensSettings.getInstance()
        if (commercialCheckbox?.isSelected != settings.isCommercialProject) return true
        if (policyCheckbox?.isSelected != settings.isPolicyEnabled) return true
        return blockedField?.text != settings.blockedLicenses
    }

    override fun apply() {
        val settings = LicenseLensSettings.getInstance()
        settings.isCommercialProject = commercialCheckbox?.isSelected ?: true
        settings.isPolicyEnabled = policyCheckbox?.isSelected ?: false
        settings.blockedLicenses = blockedField?.text ?: ""
    }

    override fun reset() {
        val settings = LicenseLensSettings.getInstance()
        commercialCheckbox?.isSelected = settings.isCommercialProject
        policyCheckbox?.isSelected = settings.isPolicyEnabled
        blockedField?.text = settings.blockedLicenses
    }

    override fun disposeUIResources() {
        commercialCheckbox = null
        policyCheckbox = null
        blockedField = null
        mainPanel = null
    }
}
