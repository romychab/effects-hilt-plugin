@file:OptIn(ExperimentalCompilerApi::class)

package com.elveum.effects.processor.base

import com.elveum.effects.processor.v2.EffectSymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

abstract class AbstractKspTest(
    private val baseResourcePath: String,
) {

    fun compileSourceFile(filePath: String): KspResult {
        val sourceFile = loadSourceFile(filePath)

        val compilation = KotlinCompilation().apply {
            sources = listOf(sourceFile)
            symbolProcessorProviders = listOf(EffectSymbolProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
        }

        return KspResult(baseResourcePath, compilation, compilation.compile())
    }

    private fun loadSourceFile(
        fileName: String
    ): SourceFile {
        val sourceContent = javaClass.getResourceAsStream("/$baseResourcePath/$fileName")?.reader().use {
            it?.readText()
        } ?: throw IllegalArgumentException("Can't open file: $fileName")
        return SourceFile.kotlin(fileName, sourceContent)
    }

}