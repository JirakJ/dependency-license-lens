package com.jakubjirak.licenselens.resolver

import com.jakubjirak.licenselens.database.LicenseDatabase
import com.jakubjirak.licenselens.model.Dependency
import com.jakubjirak.licenselens.model.LicenseCategory
import com.jakubjirak.licenselens.model.LicenseInfo
import com.jakubjirak.licenselens.model.LicenseRisk
import com.jakubjirak.licenselens.model.ResolvedDependency
import com.jakubjirak.licenselens.policy.LicensePolicy
import com.intellij.openapi.diagnostic.Logger

/**
 * Resolves license information for dependencies using the built-in database
 * and POM/package.json metadata.
 */
class LicenseResolver {

    /**
     * Resolve license for a single dependency from the built-in database.
     */
    fun resolve(dependency: Dependency, policy: LicensePolicy): ResolvedDependency {
        val license = resolveFromDatabase(dependency)
        val risk = assessRisk(license, policy)
        return ResolvedDependency(dependency = dependency, license = license, risk = risk)
    }

    /**
     * Resolve licenses for a list of dependencies.
     */
    fun resolveAll(dependencies: List<Dependency>, policy: LicensePolicy): List<ResolvedDependency> {
        LOG.info("Resolving licenses for ${dependencies.size} dependencies")
        return dependencies.map { resolve(it, policy) }
    }

    /**
     * Extract license SPDX ID from a POM XML license name.
     */
    fun resolveFromLicenseName(licenseName: String): LicenseInfo? {
        return LicenseDatabase.findByName(licenseName)
    }

    private fun resolveFromDatabase(dependency: Dependency): LicenseInfo? {
        // Try well-known artifact-to-license mappings
        val knownLicense = KNOWN_ARTIFACTS["${dependency.group}:${dependency.artifact}"]
        if (knownLicense != null) {
            return LicenseDatabase.findBySpdxId(knownLicense)
        }
        return null
    }

    private fun assessRisk(license: LicenseInfo?, policy: LicensePolicy): LicenseRisk {
        if (license == null) return LicenseRisk.CAUTION

        if (policy.isBlocked(license.spdxId)) return LicenseRisk.BLOCKED

        return when (license.category) {
            LicenseCategory.PERMISSIVE -> LicenseRisk.SAFE
            LicenseCategory.WEAK_COPYLEFT -> LicenseRisk.CAUTION
            LicenseCategory.STRONG_COPYLEFT -> {
                if (policy.isCommercialProject) LicenseRisk.WARNING else LicenseRisk.CAUTION
            }
            LicenseCategory.PROPRIETARY -> LicenseRisk.WARNING
            LicenseCategory.UNKNOWN -> LicenseRisk.CAUTION
        }
    }

    companion object {
        private val LOG = Logger.getInstance(LicenseResolver::class.java)

        private val KNOWN_ARTIFACTS = mapOf(
            "com.google.guava:guava" to "Apache-2.0",
            "com.google.code.gson:gson" to "Apache-2.0",
            "org.apache.commons:commons-lang3" to "Apache-2.0",
            "commons-io:commons-io" to "Apache-2.0",
            "org.apache.httpcomponents:httpclient" to "Apache-2.0",
            "org.slf4j:slf4j-api" to "MIT",
            "ch.qos.logback:logback-classic" to "EPL-1.0",
            "ch.qos.logback:logback-core" to "EPL-1.0",
            "com.fasterxml.jackson.core:jackson-core" to "Apache-2.0",
            "com.fasterxml.jackson.core:jackson-databind" to "Apache-2.0",
            "org.jetbrains.kotlin:kotlin-stdlib" to "Apache-2.0",
            "org.jetbrains.kotlinx:kotlinx-coroutines-core" to "Apache-2.0",
            "junit:junit" to "EPL-1.0",
            "org.junit.jupiter:junit-jupiter-api" to "EPL-2.0",
            "org.mockito:mockito-core" to "MIT",
            "org.springframework:spring-core" to "Apache-2.0",
            "org.springframework.boot:spring-boot" to "Apache-2.0",
            "io.netty:netty-all" to "Apache-2.0",
            "com.squareup.okhttp3:okhttp" to "Apache-2.0",
            "com.squareup.retrofit2:retrofit" to "Apache-2.0",
            "org.yaml:snakeyaml" to "Apache-2.0",
            "io.projectreactor:reactor-core" to "Apache-2.0",
            "com.h2database:h2" to "MPL-2.0",
            "org.postgresql:postgresql" to "BSD-2-Clause",
            "mysql:mysql-connector-java" to "GPL-2.0",
            "org.mariadb.jdbc:mariadb-java-client" to "LGPL-2.1",
        )
    }
}
