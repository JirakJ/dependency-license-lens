package com.jakubjirak.licenselens.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.jakubjirak.licenselens.policy.LicensePolicy

@State(
    name = "LicenseLensSettings",
    storages = [Storage(value = "licenseLensSettings.xml", roamingType = RoamingType.DEFAULT)],
)
class LicenseLensSettings : PersistentStateComponent<LicenseLensSettings> {

    var isCommercialProject: Boolean = true
    var blockedLicenses: String = "GPL-2.0, GPL-3.0, AGPL-3.0"
    var isPolicyEnabled: Boolean = false

    override fun getState(): LicenseLensSettings = this

    override fun loadState(state: LicenseLensSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun toPolicy(): LicensePolicy {
        val blocked = blockedLicenses
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

        return LicensePolicy(
            isCommercialProject = isCommercialProject,
            blockedLicenses = blocked,
            isPolicyEnabled = isPolicyEnabled,
        )
    }

    companion object {
        fun getInstance(): LicenseLensSettings =
            ApplicationManager.getApplication().getService(LicenseLensSettings::class.java)
    }
}
