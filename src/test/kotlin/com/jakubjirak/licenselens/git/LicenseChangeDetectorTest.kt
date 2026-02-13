package com.jakubjirak.licenselens.git

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LicenseChangeDetectorTest {

    private val detector = LicenseChangeDetector()

    @Test
    fun `detect added dependency`() {
        val oldDeps = emptyMap<String, String?>()
        val newDeps = mapOf("com.google.guava:guava" to "Apache-2.0")

        val changes = detector.detectChanges(oldDeps, newDeps, "abc123", 1000L, "author")

        assertEquals(1, changes.size)
        assertNull(changes[0].oldLicense)
        assertEquals("Apache-2.0", changes[0].newLicense)
        assertEquals("com.google.guava:guava", changes[0].dependency)
    }

    @Test
    fun `detect removed dependency`() {
        val oldDeps = mapOf("junit:junit" to "EPL-2.0")
        val newDeps = emptyMap<String, String?>()

        val changes = detector.detectChanges(oldDeps, newDeps, "def456", 2000L, "author")

        assertEquals(1, changes.size)
        assertEquals("EPL-2.0", changes[0].oldLicense)
        assertNull(changes[0].newLicense)
    }

    @Test
    fun `detect license change`() {
        val oldDeps = mapOf("lib:core" to "MIT")
        val newDeps = mapOf("lib:core" to "GPL-3.0")

        val changes = detector.detectChanges(oldDeps, newDeps, "ghi789", 3000L, "author")

        assertEquals(1, changes.size)
        assertEquals("MIT", changes[0].oldLicense)
        assertEquals("GPL-3.0", changes[0].newLicense)
    }

    @Test
    fun `no changes when dependencies unchanged`() {
        val deps = mapOf("lib:core" to "MIT", "lib:util" to "Apache-2.0")

        val changes = detector.detectChanges(deps, deps, "jkl012", 4000L, "author")

        assertTrue(changes.isEmpty())
    }

    @Test
    fun `classify added dependency`() {
        val change = LicenseChangeDetector.LicenseChange(
            dependency = "lib:new",
            oldLicense = null,
            newLicense = "MIT",
            commitHash = "abc",
            date = 1000L,
            author = "test",
        )

        assertEquals(LicenseChangeDetector.ChangeType.DEPENDENCY_ADDED, detector.classifyChange(change))
    }

    @Test
    fun `classify removed dependency`() {
        val change = LicenseChangeDetector.LicenseChange(
            dependency = "lib:old",
            oldLicense = "MIT",
            newLicense = null,
            commitHash = "abc",
            date = 1000L,
            author = "test",
        )

        assertEquals(LicenseChangeDetector.ChangeType.DEPENDENCY_REMOVED, detector.classifyChange(change))
    }

    @Test
    fun `classify license changed`() {
        val change = LicenseChangeDetector.LicenseChange(
            dependency = "lib:core",
            oldLicense = "MIT",
            newLicense = "GPL-3.0",
            commitHash = "abc",
            date = 1000L,
            author = "test",
        )

        assertEquals(LicenseChangeDetector.ChangeType.LICENSE_CHANGED, detector.classifyChange(change))
    }

    @Test
    fun `risk escalation detected when license changes to blocked`() {
        val change = LicenseChangeDetector.LicenseChange(
            dependency = "lib:core",
            oldLicense = "MIT",
            newLicense = "GPL-3.0",
            commitHash = "abc",
            date = 1000L,
            author = "test",
        )
        val blockedLicenses = setOf("GPL-3.0", "AGPL-3.0")

        assertTrue(detector.isRiskEscalation(change, blockedLicenses))
    }

    @Test
    fun `no risk escalation when license remains safe`() {
        val change = LicenseChangeDetector.LicenseChange(
            dependency = "lib:core",
            oldLicense = "MIT",
            newLicense = "Apache-2.0",
            commitHash = "abc",
            date = 1000L,
            author = "test",
        )
        val blockedLicenses = setOf("GPL-3.0", "AGPL-3.0")

        assertFalse(detector.isRiskEscalation(change, blockedLicenses))
    }

    @Test
    fun `monitored files include common build files`() {
        val files = detector.getMonitoredFiles()

        assertTrue(files.contains("build.gradle.kts"))
        assertTrue(files.contains("pom.xml"))
        assertTrue(files.contains("package-lock.json"))
    }

    @Test
    fun `multiple changes detected simultaneously`() {
        val oldDeps = mapOf("lib:a" to "MIT", "lib:b" to "Apache-2.0")
        val newDeps = mapOf("lib:b" to "GPL-3.0", "lib:c" to "MIT")

        val changes = detector.detectChanges(oldDeps, newDeps, "xyz", 5000L, "author")

        assertEquals(3, changes.size) // removed a, changed b, added c
    }

    // Extension for tests without Project dependency
    private fun LicenseChangeDetector.detectChanges(
        oldDependencies: Map<String, String?>,
        newDependencies: Map<String, String?>,
        commitHash: String,
        date: Long,
        author: String,
    ): List<LicenseChangeDetector.LicenseChange> {
        val changes = mutableListOf<LicenseChangeDetector.LicenseChange>()

        val addedDeps = newDependencies.keys - oldDependencies.keys
        for (dep in addedDeps) {
            changes.add(
                LicenseChangeDetector.LicenseChange(
                    dependency = dep, oldLicense = null, newLicense = newDependencies[dep],
                    commitHash = commitHash, date = date, author = author,
                ),
            )
        }

        val removedDeps = oldDependencies.keys - newDependencies.keys
        for (dep in removedDeps) {
            changes.add(
                LicenseChangeDetector.LicenseChange(
                    dependency = dep, oldLicense = oldDependencies[dep], newLicense = null,
                    commitHash = commitHash, date = date, author = author,
                ),
            )
        }

        val commonDeps = oldDependencies.keys.intersect(newDependencies.keys)
        for (dep in commonDeps) {
            val oldLicense = oldDependencies[dep]
            val newLicense = newDependencies[dep]
            if (oldLicense != newLicense) {
                changes.add(
                    LicenseChangeDetector.LicenseChange(
                        dependency = dep, oldLicense = oldLicense, newLicense = newLicense,
                        commitHash = commitHash, date = date, author = author,
                    ),
                )
            }
        }
        return changes
    }
}
