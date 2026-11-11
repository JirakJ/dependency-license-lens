package com.jakubjirak.licenselens.parser

import com.jakubjirak.licenselens.model.Dependency
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MavenDependencyParserTest {

    private val parser = MavenDependencyParser()

    @Test
    fun `supports only pom xml`() {
        assertTrue(parser.supports("pom.xml"))
        assertFalse(parser.supports("build.gradle"))
        assertFalse(parser.supports("pom.xml.bak"))
    }

    @Test
    fun `parses dependencies with scope and default compile scope`() {
        val pom = """
            <project xmlns="http://maven.apache.org/POM/4.0.0">
              <dependencies>
                <dependency>
                  <groupId>com.google.guava</groupId>
                  <artifactId>guava</artifactId>
                  <version>33.0.0-jre</version>
                </dependency>
                <dependency>
                  <groupId> junit </groupId>
                  <artifactId>junit</artifactId>
                  <version>4.13.2</version>
                  <scope>test</scope>
                </dependency>
              </dependencies>
            </project>
        """.trimIndent()

        assertEquals(
            listOf(
                Dependency("com.google.guava", "guava", "33.0.0-jre", "compile"),
                Dependency("junit", "junit", "4.13.2", "test"),
            ),
            parser.parse(pom),
        )
    }

    @Test
    fun `dependency without version is kept with empty version`() {
        val pom = "<project><dependencies><dependency><groupId>g</groupId><artifactId>a</artifactId></dependency></dependencies></project>"
        assertEquals(listOf(Dependency("g", "a", "", "compile")), parser.parse(pom))
    }

    @Test
    fun `dependency missing groupId or artifactId is skipped`() {
        val pom = "<project><dependencies>" +
            "<dependency><artifactId>a</artifactId></dependency>" +
            "<dependency><groupId>g</groupId></dependency>" +
            "</dependencies></project>"
        assertTrue(parser.parse(pom).isEmpty())
    }

    @Test
    fun `malformed xml yields empty list`() {
        assertTrue(parser.parse("<project><dependencies>").isEmpty())
        assertTrue(parser.parse("").isEmpty())
    }

    @Test
    fun `doctype with external entity is rejected instead of resolved`() {
        val pom = """
            <?xml version="1.0"?>
            <!DOCTYPE project [ <!ENTITY xxe SYSTEM "file:///etc/passwd"> ]>
            <project><dependencies><dependency>
              <groupId>&xxe;</groupId><artifactId>a</artifactId><version>1</version>
            </dependency></dependencies></project>
        """.trimIndent()
        assertTrue(parser.parse(pom).isEmpty())
    }
}

class NpmDependencyParserTest {

    private val parser = NpmDependencyParser()

    @Test
    fun `supports only package json`() {
        assertTrue(parser.supports("package.json"))
        assertFalse(parser.supports("package-lock.json"))
    }

    @Test
    fun `parses all dependency blocks with scopes and strips range operators`() {
        val json = """
            {
              "name": "app",
              "dependencies": { "react": "^18.2.0", "@types/node": "~20.1.0" },
              "devDependencies": { "jest": ">=29.0.0" },
              "peerDependencies": { "vue": "3.4.0" }
            }
        """.trimIndent()

        assertEquals(
            listOf(
                Dependency("", "react", "18.2.0", "runtime"),
                Dependency("@types", "node", "20.1.0", "runtime"),
                Dependency("", "jest", "29.0.0", "dev"),
                Dependency("", "vue", "3.4.0", "peer"),
            ),
            parser.parse(json),
        )
    }

    @Test
    fun `non-string version entries do not drop other dependencies`() {
        val json = """{ "dependencies": { "weird": { "version": "1" }, "lodash": "4.17.21" } }"""
        val deps = parser.parse(json)
        assertEquals(listOf("weird", "lodash"), deps.map { it.artifact })
        assertEquals("", deps[0].version)
        assertEquals("4.17.21", deps[1].version)
    }

    @Test
    fun `invalid or non-object json yields empty list`() {
        assertTrue(parser.parse("{ not json").isEmpty())
        assertTrue(parser.parse("[1, 2]").isEmpty())
        assertTrue(parser.parse("""{ "dependencies": "oops" }""").isEmpty())
    }

    @Test
    fun `package without dependency blocks yields empty list`() {
        assertTrue(parser.parse("""{ "name": "x", "version": "1.0.0" }""").isEmpty())
    }
}
