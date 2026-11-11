package com.jakubjirak.licenselens.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GradleGroovyDslParserTest {

    private val parser = GradleDependencyParser()

    @Test
    fun `parses groovy single-quoted string notation without parentheses`() {
        val deps = parser.parse("dependencies {\n    implementation 'com.google.guava:guava:33.0.0-jre'\n}")
        assertEquals(1, deps.size)
        assertEquals("com.google.guava", deps[0].group)
        assertEquals("guava", deps[0].artifact)
        assertEquals("33.0.0-jre", deps[0].version)
        assertEquals("implementation", deps[0].scope)
    }

    @Test
    fun `parses groovy double-quoted string notation without parentheses`() {
        val deps = parser.parse("testImplementation \"junit:junit:4.13.2\"")
        assertEquals(1, deps.size)
        assertEquals("testImplementation", deps[0].scope)
        assertEquals("junit", deps[0].artifact)
    }

    @Test
    fun `parses groovy map notation`() {
        val deps = parser.parse("implementation group: 'org.slf4j', name: 'slf4j-api', version: '2.0.9'")
        assertEquals(1, deps.size)
        assertEquals("org.slf4j", deps[0].group)
        assertEquals("slf4j-api", deps[0].artifact)
        assertEquals("2.0.9", deps[0].version)
    }

    @Test
    fun `version-less coordinates managed by a platform are kept with empty version`() {
        val deps = parser.parse("implementation(\"org.springframework.boot:spring-boot\")")
        assertEquals(1, deps.size)
        assertEquals("spring-boot", deps[0].artifact)
        assertEquals("", deps[0].version)
    }

    @Test
    fun `classifier coordinates keep the version`() {
        val deps = parser.parse("runtimeOnly(\"io.netty:netty-all:4.1.100.Final:linux-x86_64\")")
        assertEquals("4.1.100.Final", deps.single().version)
    }

    @Test
    fun `dependency block with exclusions closure is parsed`() {
        val deps = parser.parse("implementation(\"com.squareup.okhttp3:okhttp:4.12.0\") {\n    exclude(group = \"x\")\n}")
        assertEquals("okhttp", deps.single().artifact)
    }

    @Test
    fun `project, catalog and platform references are ignored`() {
        val content = """
            implementation(project(":core"))
            implementation(libs.guava)
            implementation(platform("org.springframework.boot:spring-boot-dependencies:3.2.0"))
            implementation 'only-group'
        """.trimIndent()
        assertTrue(parser.parse(content).isEmpty())
    }

    @Test
    fun `kapt and testCompileOnly configurations are recognised`() {
        val deps = parser.parse("kapt(\"com.google.dagger:dagger-compiler:2.50\")\ntestCompileOnly 'org.projectlombok:lombok:1.18.30'")
        assertEquals(listOf("kapt", "testCompileOnly"), deps.map { it.scope })
    }

    @Test
    fun `non-configuration lines are ignored`() {
        assertTrue(parser.parse("plugins { id(\"java\") }\nversion = \"1.0\"\napiVersion(\"a:b:c\")").isEmpty())
    }
}
