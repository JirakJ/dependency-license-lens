package com.jakubjirak.licenselens.database

import com.jakubjirak.licenselens.model.LicenseCategory
import com.jakubjirak.licenselens.model.LicenseInfo
import com.intellij.openapi.diagnostic.Logger

/**
 * Built-in SPDX license database mapping license identifiers to metadata.
 */
object LicenseDatabase {

    private val LOG = Logger.getInstance(LicenseDatabase::class.java)

    private val licenses: Map<String, LicenseInfo> = buildMap {
        // Permissive licenses
        register("MIT", "MIT License", "https://opensource.org/licenses/MIT", LicenseCategory.PERMISSIVE)
        register("Apache-2.0", "Apache License 2.0", "https://opensource.org/licenses/Apache-2.0", LicenseCategory.PERMISSIVE)
        register("BSD-2-Clause", "BSD 2-Clause License", "https://opensource.org/licenses/BSD-2-Clause", LicenseCategory.PERMISSIVE)
        register("BSD-3-Clause", "BSD 3-Clause License", "https://opensource.org/licenses/BSD-3-Clause", LicenseCategory.PERMISSIVE)
        register("ISC", "ISC License", "https://opensource.org/licenses/ISC", LicenseCategory.PERMISSIVE)
        register("Unlicense", "The Unlicense", "https://unlicense.org/", LicenseCategory.PERMISSIVE)
        register("CC0-1.0", "CC0 1.0 Universal", "https://creativecommons.org/publicdomain/zero/1.0/", LicenseCategory.PERMISSIVE)
        register("Zlib", "zlib License", "https://opensource.org/licenses/Zlib", LicenseCategory.PERMISSIVE)

        // Weak copyleft licenses
        register(
            "LGPL-2.1",
            "GNU Lesser General Public License v2.1",
            "https://opensource.org/licenses/LGPL-2.1",
            LicenseCategory.WEAK_COPYLEFT,
        )
        register(
            "LGPL-2.1-only",
            "GNU Lesser General Public License v2.1 only",
            "https://opensource.org/licenses/LGPL-2.1",
            LicenseCategory.WEAK_COPYLEFT,
        )
        register(
            "LGPL-2.1-or-later",
            "GNU Lesser General Public License v2.1 or later",
            "https://opensource.org/licenses/LGPL-2.1",
            LicenseCategory.WEAK_COPYLEFT,
        )
        register(
            "LGPL-3.0",
            "GNU Lesser General Public License v3.0",
            "https://opensource.org/licenses/LGPL-3.0",
            LicenseCategory.WEAK_COPYLEFT,
        )
        register(
            "LGPL-3.0-only",
            "GNU Lesser General Public License v3.0 only",
            "https://opensource.org/licenses/LGPL-3.0",
            LicenseCategory.WEAK_COPYLEFT,
        )
        register(
            "LGPL-3.0-or-later",
            "GNU Lesser General Public License v3.0 or later",
            "https://opensource.org/licenses/LGPL-3.0",
            LicenseCategory.WEAK_COPYLEFT,
        )
        register("MPL-2.0", "Mozilla Public License 2.0", "https://opensource.org/licenses/MPL-2.0", LicenseCategory.WEAK_COPYLEFT)
        register("EPL-2.0", "Eclipse Public License 2.0", "https://opensource.org/licenses/EPL-2.0", LicenseCategory.WEAK_COPYLEFT)
        register("EPL-1.0", "Eclipse Public License 1.0", "https://opensource.org/licenses/EPL-1.0", LicenseCategory.WEAK_COPYLEFT)
        register(
            "CDDL-1.0",
            "Common Development and Distribution License 1.0",
            "https://opensource.org/licenses/CDDL-1.0",
            LicenseCategory.WEAK_COPYLEFT,
        )

        // Strong copyleft licenses
        register("GPL-2.0", "GNU General Public License v2.0", "https://opensource.org/licenses/GPL-2.0", LicenseCategory.STRONG_COPYLEFT)
        register(
            "GPL-2.0-only",
            "GNU General Public License v2.0 only",
            "https://opensource.org/licenses/GPL-2.0",
            LicenseCategory.STRONG_COPYLEFT,
        )
        register(
            "GPL-2.0-or-later",
            "GNU General Public License v2.0 or later",
            "https://opensource.org/licenses/GPL-2.0",
            LicenseCategory.STRONG_COPYLEFT,
        )
        register("GPL-3.0", "GNU General Public License v3.0", "https://opensource.org/licenses/GPL-3.0", LicenseCategory.STRONG_COPYLEFT)
        register(
            "GPL-3.0-only",
            "GNU General Public License v3.0 only",
            "https://opensource.org/licenses/GPL-3.0",
            LicenseCategory.STRONG_COPYLEFT,
        )
        register(
            "GPL-3.0-or-later",
            "GNU General Public License v3.0 or later",
            "https://opensource.org/licenses/GPL-3.0",
            LicenseCategory.STRONG_COPYLEFT,
        )
        register(
            "AGPL-3.0",
            "GNU Affero General Public License v3.0",
            "https://opensource.org/licenses/AGPL-3.0",
            LicenseCategory.STRONG_COPYLEFT,
        )
        register(
            "AGPL-3.0-only",
            "GNU Affero General Public License v3.0 only",
            "https://opensource.org/licenses/AGPL-3.0",
            LicenseCategory.STRONG_COPYLEFT,
        )
        register(
            "AGPL-3.0-or-later",
            "GNU Affero General Public License v3.0 or later",
            "https://opensource.org/licenses/AGPL-3.0",
            LicenseCategory.STRONG_COPYLEFT,
        )
    }

    private val nameAliases: Map<String, String> = buildMap {
        put("the mit license", "MIT")
        put("mit license", "MIT")
        put("mit", "MIT")
        put("apache license, version 2.0", "Apache-2.0")
        put("apache license 2.0", "Apache-2.0")
        put("apache-2.0", "Apache-2.0")
        put("apache 2.0", "Apache-2.0")
        put("the apache software license, version 2.0", "Apache-2.0")
        put("bsd 2-clause", "BSD-2-Clause")
        put("bsd 3-clause", "BSD-3-Clause")
        put("the bsd 3-clause license", "BSD-3-Clause")
        put("gnu lesser general public license", "LGPL-2.1")
        put("lgpl", "LGPL-2.1")
        put("gnu general public license, version 2", "GPL-2.0")
        put("gnu general public license, version 3", "GPL-3.0")
        put("gpl-2.0", "GPL-2.0")
        put("gpl-3.0", "GPL-3.0")
        put("gpl", "GPL-3.0")
        put("eclipse public license 1.0", "EPL-1.0")
        put("eclipse public license 2.0", "EPL-2.0")
        put("eclipse public license - v 2.0", "EPL-2.0")
        put("mozilla public license 2.0", "MPL-2.0")
        put("common development and distribution license", "CDDL-1.0")
        put("cddl", "CDDL-1.0")
        put("isc license", "ISC")
        put("isc", "ISC")
        put("the unlicense", "Unlicense")
        put("unlicense", "Unlicense")
    }

    /**
     * Look up license info by SPDX identifier.
     */
    fun findBySpdxId(spdxId: String): LicenseInfo? {
        return licenses[spdxId]
    }

    /**
     * Look up license info by name, trying SPDX ID first, then name aliases.
     */
    fun findByName(name: String): LicenseInfo? {
        val direct = licenses[name]
        if (direct != null) return direct

        val aliasId = nameAliases[name.lowercase().trim()]
        if (aliasId != null) return licenses[aliasId]

        LOG.debug("Unknown license name: $name")
        return null
    }

    /**
     * Get the category for a given SPDX identifier.
     */
    fun getCategoryForSpdxId(spdxId: String): LicenseCategory {
        return licenses[spdxId]?.category ?: LicenseCategory.UNKNOWN
    }

    /**
     * Get all known SPDX identifiers.
     */
    fun getAllSpdxIds(): Set<String> = licenses.keys

    /**
     * Get all known licenses.
     */
    fun getAllLicenses(): Collection<LicenseInfo> = licenses.values

    private fun MutableMap<String, LicenseInfo>.register(
        spdxId: String,
        name: String,
        url: String,
        category: LicenseCategory,
    ) {
        put(spdxId, LicenseInfo(spdxId = spdxId, name = name, url = url, category = category))
    }
}
