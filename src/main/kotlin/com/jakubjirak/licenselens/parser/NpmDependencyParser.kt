package com.jakubjirak.licenselens.parser

import com.google.gson.JsonParseException
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

        val root = try {
            JsonParser.parseString(content)
        } catch (e: JsonParseException) {
            LOG.warn("Failed to parse package.json", e)
            return dependencies
        }
        if (!root.isJsonObject) return dependencies

        parseDependencyBlock(root.asJsonObject, "dependencies", "runtime", dependencies)
        parseDependencyBlock(root.asJsonObject, "devDependencies", "dev", dependencies)
        parseDependencyBlock(root.asJsonObject, "peerDependencies", "peer", dependencies)

        LOG.debug("Parsed ${dependencies.size} dependencies from package.json")
        return dependencies
    }

    private fun parseDependencyBlock(
        root: com.google.gson.JsonObject,
        blockName: String,
        scope: String,
        dependencies: MutableList<Dependency>,
    ) {
        val element = root.get(blockName)
        if (element == null || !element.isJsonObject) return

        for ((name, versionElement) in element.asJsonObject.entrySet()) {
            val rawVersion = if (versionElement.isJsonPrimitive) versionElement.asString else ""
            val version = rawVersion.trimStart('^', '~', '>', '<', '=', ' ')
            // Scoped packages: "@scope/name" -> group "@scope", artifact "name"
            val slash = name.indexOf('/')
            val group = if (slash > 0) name.substring(0, slash) else ""
            val artifact = if (slash > 0) name.substring(slash + 1) else name

            dependencies.add(Dependency(group = group, artifact = artifact, version = version, scope = scope))
        }
    }

    companion object {
        private val LOG = Logger.getInstance(NpmDependencyParser::class.java)
    }
}
