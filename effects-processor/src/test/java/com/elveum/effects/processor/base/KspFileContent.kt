package com.elveum.effects.processor.base

import org.junit.Assert.assertEquals
import java.io.File

class KspFileContent(
    private val baseResourcePath: String,
    val fileName: String,
    val content: String,
) {

    fun assertContent(expectedFilePath: String) {
        val expectedContent = javaClass
            .getResourceAsStream("/$baseResourcePath/$expectedFilePath")?.reader().use {
                it?.readText()
            }
        assertEquals(expectedContent?.cleanUpSpaces(), content.cleanUpSpaces())
    }

    internal fun replaceWithContent(expectedFilePath: String) {
        val outputFile = File("src/test/resources/$baseResourcePath/$expectedFilePath")
        outputFile.writeText(content.trim())
    }

    override fun toString(): String {
        return content
    }

    private fun String.cleanUpSpaces(): String {
        return lines()
            .map(String::trim)
            .filter(String::isNotEmpty)
            .joinToString("\n")
    }

}