package com.jakubjirak.licenselens.parser

import com.jakubjirak.licenselens.model.Dependency
import com.intellij.openapi.diagnostic.Logger

/**
 * Parses dependencies from Gradle build files (build.gradle and build.gradle.kts).
 */
class GradleDependencyParser : DependencyParser {

    override fun supports(fileName: String): Boolean {
        return fileName == "build.gradle" || fileName == "build.gradle.kts"
    }

    override fun parse(content: String): List<Dependency> {
        val dependencies = mutableListOf<Dependency>()

        for (line in content.lines()) {
            val trimmed = line.trim()
            if (trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*")) continue

            val dependency = parseConfigurationLine(trimmed)
            if (dependency != null) {
                dependencies.add(dependency)
            }
        }

        LOG.debug("Parsed ${dependencies.size} dependencies from Gradle build file")
        return dependencies
    }

    private fun parseConfigurationLine(line: String): Dependency? {
        val scope = extractScope(line) ?: return null
        return parseStringNotation(line, scope) ?: parseMethodNotation(line, scope)
    }

    private fun extractScope(line: String): String? {
        for (config in CONFIGURATIONS) {
            if (line.startsWith("$config(") || line.startsWith("$config ")) {
                return config
            }
        }
        return null
    }

    private fun parseStringNotation(line: String, scope: String): Dependency? {
        val match = STRING_NOTATION_PATTERN.find(line) ?: return null
        val coords = match.groupValues[1]
        return parseMavenCoordinates(coords, scope)
    }

    private fun parseMethodNotation(line: String, scope: String): Dependency? {
        val match = METHOD_NOTATION_PATTERN.find(line) ?: return null
        val group = match.groupValues[1]
        val artifact = match.groupValues[2]
        val version = match.groupValues[3]
        return Dependency(group = group, artifact = artifact, version = version, scope = scope)
    }

    private fun parseMavenCoordinates(coords: String, scope: String): Dependency? {
        val parts = coords.split(":")
        if (parts.size < 3) return null
        return Dependency(
            group = parts[0],
            artifact = parts[1],
            version = parts[2],
            scope = scope,
        )
    }

    companion object {
        private val LOG = Logger.getInstance(GradleDependencyParser::class.java)

        private val CONFIGURATIONS = listOf(
            "implementation",
            "api",
            "compileOnly",
            "runtimeOnly",
            "testImplementation",
            "testRuntimeOnly",
            "testCompileOnly",
            "annotationProcessor",
            "kapt",
        )

        private val CONFIGS_PATTERN = CONFIGURATIONS.joinToString("|")

        private val STRING_NOTATION_PATTERN = Regex(
            """(?:$CONFIGS_PATTERN)\s*\(\s*"([^"]+)"\s*\)"""
        )

        private val METHOD_NOTATION_PATTERN = Regex(
            "(?:$CONFIGS_PATTERN)" +
                """\s*\(\s*group\s*[:=]\s*"([^"]+)"""" +
                """\s*,\s*name\s*[:=]\s*"([^"]+)"""" +
                """\s*,\s*version\s*[:=]\s*"([^"]+)"\s*\)"""
        )
    }
}
