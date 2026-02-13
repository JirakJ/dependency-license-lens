package com.jakubjirak.licenselens.parser

import com.jakubjirak.licenselens.model.Dependency
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GradleDependencyParserTest {

    private val parser = GradleDependencyParser()

    @Test
    fun `supports build_gradle file`() {
        assertTrue(parser.supports("build.gradle"))
    }

    @Test
    fun `supports build_gradle_kts file`() {
        assertTrue(parser.supports("build.gradle.kts"))
    }

    @Test
    fun `does not support pom_xml`() {
        assertTrue(!parser.supports("pom.xml"))
    }

    @Test
    fun `parse implementation with string notation`() {
        val content = """
            dependencies {
                implementation("com.google.guava:guava:33.0.0-jre")
            }
        """.trimIndent()

        val deps = parser.parse(content)

        assertEquals(1, deps.size)
        assertEquals("com.google.guava", deps[0].group)
        assertEquals("guava", deps[0].artifact)
        assertEquals("33.0.0-jre", deps[0].version)
        assertEquals("implementation", deps[0].scope)
    }

    @Test
    fun `parse testImplementation with string notation`() {
        val content = """
            dependencies {
                testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
            }
        """.trimIndent()

        val deps = parser.parse(content)

        assertEquals(1, deps.size)
        assertEquals("org.junit.jupiter", deps[0].group)
        assertEquals("junit-jupiter-api", deps[0].artifact)
        assertEquals("5.11.0", deps[0].version)
        assertEquals("testImplementation", deps[0].scope)
    }

    @Test
    fun `parse multiple dependencies`() {
        val content = """
            dependencies {
                implementation("com.google.guava:guava:33.0.0-jre")
                implementation("com.google.code.gson:gson:2.11.0")
                testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
                testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
            }
        """.trimIndent()

        val deps = parser.parse(content)

        assertEquals(4, deps.size)
    }

    @Test
    fun `parse api configuration`() {
        val content = """
            dependencies {
                api("com.google.guava:guava:33.0.0-jre")
            }
        """.trimIndent()

        val deps = parser.parse(content)

        assertEquals(1, deps.size)
        assertEquals("api", deps[0].scope)
    }

    @Test
    fun `parse compileOnly configuration`() {
        val content = """
            dependencies {
                compileOnly("javax.servlet:javax.servlet-api:4.0.1")
            }
        """.trimIndent()

        val deps = parser.parse(content)

        assertEquals(1, deps.size)
        assertEquals("compileOnly", deps[0].scope)
    }

    @Test
    fun `parse runtimeOnly configuration`() {
        val content = """
            dependencies {
                runtimeOnly("org.postgresql:postgresql:42.7.1")
            }
        """.trimIndent()

        val deps = parser.parse(content)

        assertEquals(1, deps.size)
        assertEquals("runtimeOnly", deps[0].scope)
    }

    @Test
    fun `skip comments`() {
        val content = """
            dependencies {
                // implementation("commented:out:1.0")
                /* implementation("block:comment:1.0") */
                implementation("real:dependency:2.0")
            }
        """.trimIndent()

        val deps = parser.parse(content)

        assertEquals(1, deps.size)
        assertEquals("real", deps[0].group)
    }

    @Test
    fun `parse empty file`() {
        val deps = parser.parse("")

        assertTrue(deps.isEmpty())
    }

    @Test
    fun `parse file without dependencies block`() {
        val content = """
            plugins {
                kotlin("jvm") version "2.1.0"
            }

            group = "com.example"
            version = "1.0.0"
        """.trimIndent()

        val deps = parser.parse(content)

        assertTrue(deps.isEmpty())
    }

    @Test
    fun `parse method notation with group name version`() {
        val content = """
            dependencies {
                implementation(group = "com.google.guava", name = "guava", version = "33.0.0-jre")
            }
        """.trimIndent()

        val deps = parser.parse(content)

        assertEquals(1, deps.size)
        assertEquals("com.google.guava", deps[0].group)
        assertEquals("guava", deps[0].artifact)
        assertEquals("33.0.0-jre", deps[0].version)
    }

    @Test
    fun `parse groovy style string notation`() {
        val content = """
            dependencies {
                implementation("org.apache.commons:commons-lang3:3.14.0")
            }
        """.trimIndent()

        val deps = parser.parse(content)

        assertEquals(1, deps.size)
        assertEquals("org.apache.commons", deps[0].group)
        assertEquals("commons-lang3", deps[0].artifact)
    }

    @Test
    fun `parse annotationProcessor configuration`() {
        val content = """
            dependencies {
                annotationProcessor("org.projectlombok:lombok:1.18.30")
            }
        """.trimIndent()

        val deps = parser.parse(content)

        assertEquals(1, deps.size)
        assertEquals("annotationProcessor", deps[0].scope)
    }
}
