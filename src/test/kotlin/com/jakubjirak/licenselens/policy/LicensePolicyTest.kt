package com.jakubjirak.licenselens.policy

import com.jakubjirak.licenselens.database.LicenseDatabase
import com.jakubjirak.licenselens.model.Dependency
import com.jakubjirak.licenselens.model.LicenseRisk
import com.jakubjirak.licenselens.model.ResolvedDependency
import com.jakubjirak.licenselens.settings.LicenseLensSettings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LicensePolicyTest {

    private val dep = Dependency("g", "a", "1")

    @Test
    fun `isBlocked only when enabled and listed`() {
        val enabled = LicensePolicy(blockedLicenses = setOf("GPL-3.0"), isPolicyEnabled = true)
        val disabled = LicensePolicy(blockedLicenses = setOf("GPL-3.0"), isPolicyEnabled = false)
        assertTrue(enabled.isBlocked("GPL-3.0"))
        assertFalse(enabled.isBlocked("MIT"))
        assertFalse(disabled.isBlocked("GPL-3.0"))
    }

    @Test
    fun `isBlocked is an exact SPDX match`() {
        val policy = LicensePolicy(blockedLicenses = setOf("GPL-3.0"), isPolicyEnabled = true)
        assertFalse(policy.isBlocked("GPL-3.0-only"))
        assertFalse(policy.isBlocked("gpl-3.0"))
    }

    @Test
    fun `default policy blocks GPL family but is disabled`() {
        assertFalse(LicensePolicy.DEFAULT.isPolicyEnabled)
        assertTrue(LicensePolicy.DEFAULT.isCommercialProject)
        assertFalse(LicensePolicy.DEFAULT.isBlocked("GPL-3.0"))
    }

    @Test
    fun `evaluate reports blocked, warning and unresolved dependencies only`() {
        val gpl = LicenseDatabase.findBySpdxId("GPL-3.0")
        val mit = LicenseDatabase.findBySpdxId("MIT")
        val lgpl = LicenseDatabase.findBySpdxId("LGPL-2.1")
        val resolved = listOf(
            ResolvedDependency(dep, gpl, LicenseRisk.BLOCKED),
            ResolvedDependency(dep, gpl, LicenseRisk.WARNING),
            ResolvedDependency(dep, mit, LicenseRisk.SAFE),
            ResolvedDependency(dep, lgpl, LicenseRisk.CAUTION),
            ResolvedDependency(dep, null, LicenseRisk.CAUTION),
        )

        val issues = LicensePolicy().evaluate(resolved)

        assertEquals(listOf(LicenseRisk.BLOCKED, LicenseRisk.WARNING, LicenseRisk.CAUTION), issues.map { it.risk })
        assertEquals("License GPL-3.0 is blocked by policy", issues[0].message)
        assertTrue(issues[1].message.contains("GPL-3.0"))
        assertEquals("License could not be resolved for g:a", issues[2].message)
    }

    @Test
    fun `evaluate handles blocked risk without license`() {
        val issues = LicensePolicy().evaluate(listOf(ResolvedDependency(dep, null, LicenseRisk.BLOCKED)))
        assertEquals("License unknown is blocked by policy", issues.single().message)
    }

    @Test
    fun `evaluate of empty list is empty`() {
        assertTrue(LicensePolicy().evaluate(emptyList()).isEmpty())
    }
}

class LicenseLensSettingsTest {

    @Test
    fun `defaults map to a disabled commercial policy blocking GPL family`() {
        val policy = LicenseLensSettings().apply { isPolicyEnabled = true }.toPolicy()
        assertTrue(policy.isCommercialProject)
        assertTrue(policy.isBlocked("GPL-2.0"))
        assertTrue(policy.isBlocked("GPL-3.0"))
        assertTrue(policy.isBlocked("AGPL-3.0"))
        assertFalse(LicenseLensSettings().toPolicy().isPolicyEnabled)
    }

    @Test
    fun `blocked list is trimmed and empty entries dropped`() {
        val settings = LicenseLensSettings().apply {
            isPolicyEnabled = true
            isCommercialProject = false
            blockedLicenses = "  MIT ,, ,Apache-2.0,"
        }
        val policy = settings.toPolicy()
        assertFalse(policy.isCommercialProject)
        assertTrue(policy.isBlocked("MIT"))
        assertTrue(policy.isBlocked("Apache-2.0"))
        assertFalse(policy.isBlocked(""))
        assertFalse(policy.isBlocked("GPL-3.0"))
    }

    @Test
    fun `loadState copies all fields`() {
        val source = LicenseLensSettings().apply {
            isCommercialProject = false
            isPolicyEnabled = true
            blockedLicenses = "MPL-2.0"
        }
        val target = LicenseLensSettings()
        target.loadState(source)
        assertFalse(target.isCommercialProject)
        assertTrue(target.isPolicyEnabled)
        assertEquals("MPL-2.0", target.blockedLicenses)
        assertTrue(target.state === target)
    }
}
