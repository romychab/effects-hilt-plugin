@file:OptIn(ExperimentalCompilerApi::class)

package com.elveum.effects.processor.base

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.kspSourcesDir
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertTrue
import java.io.File

class KspResult(
    private val baseResourcePath: String,
    private val compilation: KotlinCompilation,
    originResult: KotlinCompilation.Result,
) {
    val exitCode = originResult.exitCode
    val messages = originResult.messages

    val kspGeneratedFiles: List<KspFileContent> by lazy {
        val dir = File(compilation.kspSourcesDir, "kotlin")
        dir.listGeneratedFiles()
            .map { file ->
                KspFileContent(
                    baseResourcePath = baseResourcePath,
                    fileName = file.name,
                    content = file.readText(),
                )
            }
    }

    fun getGeneratedFile(name: String): KspFileContent {
        return kspGeneratedFiles.firstOrNull { it.fileName == name }
            ?: throw IllegalArgumentException("Can't find a file: $name")
    }

    fun assertErrorLogged(log: String) {
        assertTrue(messages.contains(log))
    }

    private fun File.listGeneratedFiles(): List<File> {
        return if (isFile) {
            listOf(this)
        } else {
            listFiles()?.flatMap {
                it.listGeneratedFiles()
            } ?: emptyList()
        }
    }

}