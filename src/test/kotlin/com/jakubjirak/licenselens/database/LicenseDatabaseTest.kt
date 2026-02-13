package com.jakubjirak.licenselens.database

import com.jakubjirak.licenselens.model.LicenseCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class LicenseDatabaseTest {

    @Test
    fun `find MIT license by SPDX ID`() {
        val license = LicenseDatabase.findBySpdxId("MIT")

        assertNotNull(license)
        assertEquals("MIT", license!!.spdxId)
        assertEquals("MIT License", license.name)
        assertEquals(LicenseCategory.PERMISSIVE, license.category)
    }

    @Test
    fun `find Apache-2 license by SPDX ID`() {
        val license = LicenseDatabase.findBySpdxId("Apache-2.0")

        assertNotNull(license)
        assertEquals("Apache-2.0", license!!.spdxId)
        assertEquals(LicenseCategory.PERMISSIVE, license.category)
    }

    @Test
    fun `find GPL-3 license by SPDX ID`() {
        val license = LicenseDatabase.findBySpdxId("GPL-3.0")

        assertNotNull(license)
        assertEquals("GPL-3.0", license!!.spdxId)
        assertEquals(LicenseCategory.STRONG_COPYLEFT, license.category)
    }

    @Test
    fun `find LGPL-2_1 license by SPDX ID`() {
        val license = LicenseDatabase.findBySpdxId("LGPL-2.1")

        assertNotNull(license)
        assertEquals(LicenseCategory.WEAK_COPYLEFT, license!!.category)
    }

    @Test
    fun `find MPL-2 license by SPDX ID`() {
        val license = LicenseDatabase.findBySpdxId("MPL-2.0")

        assertNotNull(license)
        assertEquals(LicenseCategory.WEAK_COPYLEFT, license!!.category)
    }

    @Test
    fun `find AGPL-3 license by SPDX ID`() {
        val license = LicenseDatabase.findBySpdxId("AGPL-3.0")

        assertNotNull(license)
        assertEquals(LicenseCategory.STRONG_COPYLEFT, license!!.category)
    }

    @Test
    fun `unknown SPDX ID returns null`() {
        val license = LicenseDatabase.findBySpdxId("NONEXISTENT-1.0")

        assertNull(license)
    }

    @Test
    fun `find license by name alias - MIT`() {
        val license = LicenseDatabase.findByName("The MIT License")

        assertNotNull(license)
        assertEquals("MIT", license!!.spdxId)
    }

    @Test
    fun `find license by name alias - Apache`() {
        val license = LicenseDatabase.findByName("Apache License, Version 2.0")

        assertNotNull(license)
        assertEquals("Apache-2.0", license!!.spdxId)
    }

    @Test
    fun `find license by name alias - The Apache Software License`() {
        val license = LicenseDatabase.findByName("The Apache Software License, Version 2.0")

        assertNotNull(license)
        assertEquals("Apache-2.0", license!!.spdxId)
    }

    @Test
    fun `find license by name alias case insensitive`() {
        val license = LicenseDatabase.findByName("mit license")

        assertNotNull(license)
        assertEquals("MIT", license!!.spdxId)
    }

    @Test
    fun `find license by direct SPDX ID via findByName`() {
        val license = LicenseDatabase.findByName("BSD-3-Clause")

        assertNotNull(license)
        assertEquals("BSD-3-Clause", license!!.spdxId)
        assertEquals(LicenseCategory.PERMISSIVE, license.category)
    }

    @Test
    fun `unknown name returns null`() {
        val license = LicenseDatabase.findByName("Some Unknown License v42")

        assertNull(license)
    }

    @Test
    fun `getCategoryForSpdxId returns correct category`() {
        assertEquals(LicenseCategory.PERMISSIVE, LicenseDatabase.getCategoryForSpdxId("MIT"))
        assertEquals(LicenseCategory.PERMISSIVE, LicenseDatabase.getCategoryForSpdxId("Apache-2.0"))
        assertEquals(LicenseCategory.WEAK_COPYLEFT, LicenseDatabase.getCategoryForSpdxId("LGPL-2.1"))
        assertEquals(LicenseCategory.STRONG_COPYLEFT, LicenseDatabase.getCategoryForSpdxId("GPL-2.0"))
        assertEquals(LicenseCategory.UNKNOWN, LicenseDatabase.getCategoryForSpdxId("NONEXISTENT"))
    }

    @Test
    fun `getAllSpdxIds returns non-empty set`() {
        val ids = LicenseDatabase.getAllSpdxIds()

        assert(ids.isNotEmpty())
        assert(ids.contains("MIT"))
        assert(ids.contains("Apache-2.0"))
        assert(ids.contains("GPL-3.0"))
    }

    @Test
    fun `getAllLicenses returns non-empty collection`() {
        val licenses = LicenseDatabase.getAllLicenses()

        assert(licenses.isNotEmpty())
    }

    @Test
    fun `permissive licenses are correctly categorized`() {
        val permissiveIds = listOf("MIT", "Apache-2.0", "BSD-2-Clause", "BSD-3-Clause", "ISC", "Unlicense")
        for (id in permissiveIds) {
            val license = LicenseDatabase.findBySpdxId(id)
            assertNotNull(license, "License $id should exist")
            assertEquals(LicenseCategory.PERMISSIVE, license!!.category, "License $id should be PERMISSIVE")
        }
    }

    @Test
    fun `weak copyleft licenses are correctly categorized`() {
        val weakCopyleftIds = listOf("LGPL-2.1", "LGPL-3.0", "MPL-2.0", "EPL-2.0")
        for (id in weakCopyleftIds) {
            val license = LicenseDatabase.findBySpdxId(id)
            assertNotNull(license, "License $id should exist")
            assertEquals(LicenseCategory.WEAK_COPYLEFT, license!!.category, "License $id should be WEAK_COPYLEFT")
        }
    }

    @Test
    fun `strong copyleft licenses are correctly categorized`() {
        val strongCopyleftIds = listOf("GPL-2.0", "GPL-3.0", "AGPL-3.0")
        for (id in strongCopyleftIds) {
            val license = LicenseDatabase.findBySpdxId(id)
            assertNotNull(license, "License $id should exist")
            assertEquals(LicenseCategory.STRONG_COPYLEFT, license!!.category, "License $id should be STRONG_COPYLEFT")
        }
    }
}
