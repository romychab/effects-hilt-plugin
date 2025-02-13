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
        assertEquals(expectedContent?.trim(), content.trim())
    }

    override fun toString(): String {
        return content
    }

}