@file:OptIn(ExperimentalCompilerApi::class)

package com.elveum.effects.processor

import com.elveum.effects.processor.v2.EffectSymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.Result
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertTrue

abstract class AbstractKspTest {

    protected fun Result.assertErrorLogged(log: String) {
        assertTrue(messages.contains(log))
    }

    protected fun compileSourceFile(filePath: String): KotlinCompilation.Result {
        val sourceFile = loadSourceFile(filePath)

        val compilation = KotlinCompilation().apply {
            sources = listOf(sourceFile)
            symbolProcessorProviders = listOf(EffectSymbolProcessorProvider())
            inheritClassPath = true
            messageOutputStream = System.out
        }

        return compilation.compile()
    }

    private fun loadSourceFile(
        fileName: String
    ): SourceFile {
        val sourceContent = javaClass.getResourceAsStream("/$fileName")?.reader().use {
            it?.readText()
        } ?: throw IllegalArgumentException("Can't open file: $fileName")
        return SourceFile.kotlin(fileName, sourceContent)
    }

}