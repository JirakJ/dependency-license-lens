package com.jakubjirak.licenselens.licensing

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service

/**
 * Central gate for freemium feature access.
 * Free tier: scan and view licenses.
 * Paid tier: custom policies, export, CI integration.
 */
@Service(Service.Level.APP)
class FreemiumGate {

    enum class LicenseStatus {
        FREE, TRIAL, PAID, EXPIRED
    }

    /**
     * Check if a paid feature is available for the current license.
     */
    @Suppress("UNUSED_PARAMETER")
    fun isPaidFeatureAvailable(featureId: String): Boolean {
        val status = getLicenseStatus()
        return status == LicenseStatus.PAID || status == LicenseStatus.TRIAL
    }

    fun getLicenseStatus(): LicenseStatus {
        // TODO: Integrate with JetBrains Marketplace Licensing API
        // For now, return FREE — all paid features gated behind this check
        return LicenseStatus.FREE
    }

    fun getMaxFreeDependencies(): Int {
        return when (getLicenseStatus()) {
            LicenseStatus.FREE -> MAX_FREE_DEPENDENCIES
            LicenseStatus.EXPIRED -> MAX_FREE_DEPENDENCIES
            LicenseStatus.TRIAL, LicenseStatus.PAID -> Int.MAX_VALUE
        }
    }

    companion object {
        const val MAX_FREE_DEPENDENCIES = 50

        // Feature IDs
        const val FEATURE_CUSTOM_POLICY = "custom_policy"
        const val FEATURE_EXPORT = "export"
        const val FEATURE_CI_INTEGRATION = "ci_integration"
        const val FEATURE_UNLIMITED_DEPS = "unlimited_deps"
        const val FEATURE_ALL_BUILD_SYSTEMS = "all_build_systems"
        const val FEATURE_FULL_SPDX_DATABASE = "full_spdx_database"

        fun getInstance(): FreemiumGate =
            ApplicationManager.getApplication().getService(FreemiumGate::class.java)
    }
}
