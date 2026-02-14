package com.jakubjirak.licenselens.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.ui.dsl.builder.*

class LicenseLensConfigurable : BoundConfigurable("Dependency License Lens") {

    private val settings get() = LicenseLensSettings.getInstance()

    override fun createPanel() = panel {
        group("License Policy") {
            row {
                checkBox("Commercial project")
                    .bindSelected(settings::isCommercialProject)
                    .comment("Flag copyleft licenses that may conflict with commercial use")
            }
            row {
                checkBox("Enable license policy enforcement")
                    .bindSelected(settings::isPolicyEnabled)
                    .comment("Enforce blocked license rules and show violations in the editor")
            }
        }
        group("Blocked Licenses") {
            row("Blocked licenses:") {
                expandableTextField()
                    .bindText(settings::blockedLicenses)
                    .comment("Comma-separated list of SPDX identifiers to block (e.g. GPL-2.0, GPL-3.0, AGPL-3.0)")
                    .align(Align.FILL)
            }
        }
    }
}
