package com.jakubjirak.licenselens.parser

import com.jakubjirak.licenselens.model.Dependency

/**
 * Interface for parsing dependencies from build files.
 */
interface DependencyParser {

    /**
     * Parse dependencies from the given file content.
     */
    fun parse(content: String): List<Dependency>

    /**
     * Check if this parser supports the given file name.
     */
    fun supports(fileName: String): Boolean
}
