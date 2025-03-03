@file:OptIn(ExperimentalCompilerApi::class)

package com.elveum.effects.processor.base

import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

fun AbstractKspTest.runGenerationTest(
    block: GenerationTestScope.() -> Unit,
) {
    GenerationTestScopeImpl(this).apply(block)
}

interface GenerationTestScope {

    val result: KspResult

    fun compileResourceSubPackage(
        packageName: String,
        inputName: String = "Input.kt",
    )

    fun assertGeneratedFile(
        fileName: String,
        expectedOutputFileName: String = "Output.kt",
    )

    fun assertFileIsNotGenerated(fileName: String)

    fun assertCompilationCompletes()

    fun assertCompilationFails(
        expectedMessage: String = "",
    )
}

private class GenerationTestScopeImpl(
    private val abstractKspTest: AbstractKspTest,
) : GenerationTestScope {

    private lateinit var packageName: String
    override lateinit var result: KspResult

    override fun compileResourceSubPackage(
        packageName: String,
        inputName: String,
    ) {
        this.packageName = packageName
        this.result = abstractKspTest.compileSourceFile("$packageName/$inputName")
    }

    override fun assertGeneratedFile(
        fileName: String,
        expectedOutputFileName: String,
    ) {
        val generatedMediator = result.getGeneratedFile(fileName)
        assertEquals(ExitCode.OK, this.result.exitCode)
        generatedMediator.assertContent("$packageName/$expectedOutputFileName")
    }

    override fun assertCompilationCompletes() {
        assertEquals(ExitCode.OK, this.result.exitCode)
    }

    override fun assertCompilationFails(expectedMessage: String) {
        assertEquals(ExitCode.COMPILATION_ERROR, this.result.exitCode)
        this.result.assertErrorLogged(expectedMessage)
    }

    override fun assertFileIsNotGenerated(fileName: String) {
        assertTrue(result.kspGeneratedFiles.all { it.fileName != fileName })
    }

}