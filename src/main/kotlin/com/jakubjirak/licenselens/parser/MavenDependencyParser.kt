package com.jakubjirak.licenselens.parser

import com.jakubjirak.licenselens.model.Dependency
import com.intellij.openapi.diagnostic.Logger
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Parses dependencies from Maven pom.xml files.
 */
class MavenDependencyParser : DependencyParser {

    override fun supports(fileName: String): Boolean {
        return fileName == "pom.xml"
    }

    override fun parse(content: String): List<Dependency> {
        val dependencies = mutableListOf<Dependency>()

        try {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = false
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(org.xml.sax.InputSource(StringReader(content)))

            val depElements = document.getElementsByTagName("dependency")
            for (i in 0 until depElements.length) {
                val dep = parseDependencyElement(depElements.item(i))
                if (dep != null) {
                    dependencies.add(dep)
                }
            }
        } catch (e: Exception) {
            LOG.warn("Failed to parse Maven pom.xml", e)
        }

        LOG.debug("Parsed ${dependencies.size} dependencies from pom.xml")
        return dependencies
    }

    private fun parseDependencyElement(depElement: org.w3c.dom.Node): Dependency? {
        val children = depElement.childNodes

        var groupId = ""
        var artifactId = ""
        var version = ""
        var scope = "compile"

        for (j in 0 until children.length) {
            val child = children.item(j)
            when (child.nodeName) {
                "groupId" -> groupId = child.textContent.trim()
                "artifactId" -> artifactId = child.textContent.trim()
                "version" -> version = child.textContent.trim()
                "scope" -> scope = child.textContent.trim()
            }
        }

        if (groupId.isEmpty() || artifactId.isEmpty()) return null
        return Dependency(group = groupId, artifact = artifactId, version = version, scope = scope)
    }

    companion object {
        private val LOG = Logger.getInstance(MavenDependencyParser::class.java)
    }
}
