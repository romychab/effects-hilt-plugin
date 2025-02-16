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
        dir.listFiles()
            ?.mapNotNull { file ->
                KspFileContent(
                    baseResourcePath = baseResourcePath,
                    fileName = file.name,
                    content = file.readText(),
                )
            }
            ?: emptyList()
    }

    fun getGeneratedFile(name: String): KspFileContent {
        return kspGeneratedFiles.first { it.fileName == name }
    }

    fun assertErrorLogged(log: String) {
        assertTrue(messages.contains(log))
    }

}