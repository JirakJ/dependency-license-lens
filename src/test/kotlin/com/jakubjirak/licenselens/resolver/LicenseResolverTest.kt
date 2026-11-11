package com.jakubjirak.licenselens.resolver

import com.jakubjirak.licenselens.model.Dependency
import com.jakubjirak.licenselens.model.LicenseRisk
import com.jakubjirak.licenselens.policy.LicensePolicy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class LicenseResolverTest {

    private val resolver = LicenseResolver()
    private val noPolicy = LicensePolicy(isCommercialProject = true, isPolicyEnabled = false)

    private fun dep(coords: String): Dependency {
        val (g, a) = coords.split(":")
        return Dependency(g, a, "1.0")
    }

    @Test
    fun `known permissive artifact is safe`() {
        val r = resolver.resolve(dep("com.google.guava:guava"), noPolicy)
        assertEquals("Apache-2.0", r.license?.spdxId)
        assertEquals(LicenseRisk.SAFE, r.risk)
    }

    @Test
    fun `weak copyleft artifact is caution`() {
        assertEquals(LicenseRisk.CAUTION, resolver.resolve(dep("org.mariadb.jdbc:mariadb-java-client"), noPolicy).risk)
    }

    @Test
    fun `strong copyleft is warning for commercial and caution for non-commercial projects`() {
        val mysql = dep("mysql:mysql-connector-java")
        assertEquals(LicenseRisk.WARNING, resolver.resolve(mysql, noPolicy).risk)
        assertEquals(LicenseRisk.CAUTION, resolver.resolve(mysql, LicensePolicy(isCommercialProject = false)).risk)
    }

    @Test
    fun `blocked license wins over category when policy is enabled`() {
        val policy = LicensePolicy(blockedLicenses = setOf("GPL-2.0"), isPolicyEnabled = true)
        assertEquals(LicenseRisk.BLOCKED, resolver.resolve(dep("mysql:mysql-connector-java"), policy).risk)
    }

    @Test
    fun `blocked list is ignored while policy is disabled`() {
        val policy = LicensePolicy(blockedLicenses = setOf("Apache-2.0"), isPolicyEnabled = false)
        assertEquals(LicenseRisk.SAFE, resolver.resolve(dep("com.google.guava:guava"), policy).risk)
    }

    @Test
    fun `unknown artifact has no license and caution risk`() {
        val r = resolver.resolve(dep("com.example:internal-lib"), noPolicy)
        assertNull(r.license)
        assertEquals(LicenseRisk.CAUTION, r.risk)
    }

    @Test
    fun `lookup ignores version and scope`() {
        val a = resolver.resolve(Dependency("org.slf4j", "slf4j-api", "1.7.0", "api"), noPolicy)
        val b = resolver.resolve(Dependency("org.slf4j", "slf4j-api", "", "testImplementation"), noPolicy)
        assertEquals(a.license, b.license)
        assertEquals("MIT", a.license?.spdxId)
    }

    @Test
    fun `resolveAll keeps order and size`() {
        val deps = listOf(dep("junit:junit"), dep("x:y"), dep("com.h2database:h2"))
        val resolved = resolver.resolveAll(deps, noPolicy)
        assertEquals(deps, resolved.map { it.dependency })
        assertEquals(listOf("EPL-1.0", null, "MPL-2.0"), resolved.map { it.license?.spdxId })
    }

    @Test
    fun `resolveFromLicenseName maps pom license names`() {
        assertEquals("Apache-2.0", resolver.resolveFromLicenseName("The Apache Software License, Version 2.0")?.spdxId)
        assertNull(resolver.resolveFromLicenseName("Some Custom License"))
    }
}
