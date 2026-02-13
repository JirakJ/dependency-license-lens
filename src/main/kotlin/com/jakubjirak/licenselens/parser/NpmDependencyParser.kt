package com.jakubjirak.licenselens.parser

import com.google.gson.JsonParser
import com.jakubjirak.licenselens.model.Dependency
import com.intellij.openapi.diagnostic.Logger

/**
 * Parses dependencies from npm package.json files.
 */
class NpmDependencyParser : DependencyParser {

    override fun supports(fileName: String): Boolean {
        return fileName == "package.json"
    }

    override fun parse(content: String): List<Dependency> {
        val dependencies = mutableListOf<Dependency>()

        try {
            val root = JsonParser.parseString(content).asJsonObject

            parseDependencyBlock(root, "dependencies", "runtime", dependencies)
            parseDependencyBlock(root, "devDependencies", "dev", dependencies)
            parseDependencyBlock(root, "peerDependencies", "peer", dependencies)
        } catch (e: Exception) {
            LOG.warn("Failed to parse package.json", e)
        }

        LOG.debug("Parsed ${dependencies.size} dependencies from package.json")
        return dependencies
    }

    @Suppress("UNUSED_PARAMETER")
    private fun parseDependencyBlock(
        root: com.google.gson.JsonObject,
        blockName: String,
        scope: String,
        dependencies: MutableList<Dependency>,
    ) {
        val block = root.getAsJsonObject(blockName) ?: return

        for ((name, versionElement) in block.entrySet()) {
            val version = versionElement.asString.trimStart('^', '~', '>', '<', '=', ' ')
            val parts = name.split("/")
            val group = if (parts.size > 1) parts[0] else ""
            val artifact = if (parts.size > 1) parts[1] else name

            dependencies.add(Dependency(group = group, artifact = artifact, version = version, scope = scope))
        }
    }

    companion object {
        private val LOG = Logger.getInstance(NpmDependencyParser::class.java)
    }
}
