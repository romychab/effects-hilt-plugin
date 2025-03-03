package com.elveum.effects.processor.base

import org.junit.Assert.assertEquals

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