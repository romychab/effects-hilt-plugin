@file:OptIn(ExperimentalCompilerApi::class)

package com.elveum.effects.processor.base

import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals

sealed class KspTestMode {

    internal abstract fun execute(
        packageName: String,
        generatedFileName: String,
        resourceFileName: String,
        result: KspResult,
    )

    data object Assertion : KspTestMode() {
        override fun execute(
            packageName: String,
            generatedFileName: String,
            resourceFileName: String,
            result: KspResult
        ) {
            val generatedFile = result.getGeneratedFile(generatedFileName)
            assertEquals(ExitCode.OK, result.exitCode)
            generatedFile.assertContent("$packageName/$resourceFileName")
        }
    }

    data object Recording : KspTestMode() {
        override fun execute(
            packageName: String,
            generatedFileName: String,
            resourceFileName: String,
            result: KspResult
        ) {
            val generatedFile = result.getGeneratedFile(generatedFileName)
            generatedFile.replaceWithContent("$packageName/$resourceFileName")
        }
    }

}