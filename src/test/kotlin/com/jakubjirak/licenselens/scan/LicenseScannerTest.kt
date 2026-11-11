package com.jakubjirak.licenselens.scan

import com.jakubjirak.licenselens.git.StagedLicenseChecker
import com.jakubjirak.licenselens.model.LicenseRisk
import com.jakubjirak.licenselens.policy.LicensePolicy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LicenseScannerTest {

    private val scanner = LicenseScanner()

    private val gradle = "build.gradle.kts" to """
        dependencies {
            implementation("com.google.guava:guava:33.0.0-jre")
            implementation("mysql:mysql-connector-java:8.0.33")
            implementation("org.mariadb.jdbc:mariadb-java-client:3.3.0")
            implementation("com.example:internal:1.0")
        }
    """.trimIndent()

    private val npm = "package.json" to """{ "dependencies": { "react": "^18.2.0" } }"""

    @Test
    fun `supported file names`() {
        LicenseScanner.BUILD_FILE_NAMES.forEach { assertTrue(scanner.isSupported(it), it) }
        assertFalse(scanner.isSupported("requirements.txt"))
        assertFalse(scanner.isSupported("settings.gradle.kts"))
    }

    @Test
    fun `parseAll merges files and ignores unsupported ones`() {
        val deps = scanner.parseAll(listOf(gradle, npm, "README.md" to "implementation(\"a:b:c\")"))
        assertEquals(listOf("guava", "mysql-connector-java", "mariadb-java-client", "internal", "react"), deps.map { it.artifact })
    }

    @Test
    fun `scan builds summary and issues for commercial project`() {
        val report = scanner.scan(listOf(gradle), LicensePolicy(isCommercialProject = true))

        assertEquals(
            listOf(LicenseRisk.SAFE, LicenseRisk.WARNING, LicenseRisk.CAUTION, LicenseRisk.CAUTION),
            report.resolvedDependencies.map { it.risk },
        )
        with(report.summary) {
            assertEquals(4, totalDependencies)
            assertEquals(1, permissiveCount)
            assertEquals(1, weakCopyleftCount)
            assertEquals(1, strongCopyleftCount)
            assertEquals(1, unknownCount)
            assertEquals(1, issueCount)
        }
        // WARNING for GPL + CAUTION for the unresolved artifact
        assertEquals(2, report.issues.size)
    }

    @Test
    fun `scan with enabled policy marks blocked licenses`() {
        val policy = LicensePolicy(blockedLicenses = setOf("GPL-2.0"), isPolicyEnabled = true)
        val report = scanner.scan(listOf(gradle), policy)
        assertEquals(LicenseRisk.BLOCKED, report.resolvedDependencies[1].risk)
        assertEquals(1, report.summary.issueCount)
    }

    @Test
    fun `scan of nothing is an empty report`() {
        val report = scanner.scan(emptyList(), LicensePolicy.DEFAULT)
        assertTrue(report.resolvedDependencies.isEmpty())
        assertTrue(report.issues.isEmpty())
        assertEquals(0, report.summary.totalDependencies)
    }
}

class StagedLicenseCheckerTest {

    private val checker = StagedLicenseChecker()
    private val blockGpl = LicensePolicy(blockedLicenses = setOf("GPL-2.0"), isPolicyEnabled = true)
    private val pom = "pom.xml" to """
        <project><dependencies>
          <dependency><groupId>mysql</groupId><artifactId>mysql-connector-java</artifactId><version>8</version></dependency>
          <dependency><groupId>junit</groupId><artifactId>junit</artifactId><version>4</version></dependency>
        </dependencies></project>
    """.trimIndent()

    @Test
    fun `monitors exactly the parseable build files`() {
        assertTrue(checker.isMonitored("pom.xml"))
        assertTrue(checker.isMonitored("build.gradle"))
        assertFalse(checker.isMonitored("go.sum"))
    }

    @Test
    fun `blocked dependency fails the check with details`() {
        val result = checker.check(listOf(pom), blockGpl)
        assertFalse(result.passed)
        val blocked = result.blockedDependencies.single()
        assertEquals("mysql:mysql-connector-java", blocked.dependency)
        assertEquals("GPL-2.0", blocked.license)
        assertEquals("pom.xml", blocked.sourceFile)
        assertTrue(result.describe().contains("mysql:mysql-connector-java — GPL-2.0 (pom.xml)"))
    }

    @Test
    fun `disabled policy always passes`() {
        val disabled = LicensePolicy(blockedLicenses = setOf("GPL-2.0"), isPolicyEnabled = false)
        assertTrue(checker.check(listOf(pom), disabled).passed)
    }

    @Test
    fun `files without blocked licenses pass`() {
        val gradle = "build.gradle" to "implementation 'com.google.guava:guava:33.0.0-jre'"
        assertTrue(checker.check(listOf(gradle), blockGpl).passed)
        assertTrue(checker.check(emptyList(), blockGpl).passed)
    }

    @Test
    fun `same blocked dependency in two files is reported per file`() {
        val gradle = "build.gradle" to "implementation 'mysql:mysql-connector-java:8.0.33'"
        val result = checker.check(listOf(pom, gradle), blockGpl)
        assertEquals(listOf("pom.xml", "build.gradle"), result.blockedDependencies.map { it.sourceFile })
    }
}
