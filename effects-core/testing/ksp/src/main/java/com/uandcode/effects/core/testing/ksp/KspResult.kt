@file:OptIn(ExperimentalCompilerApi::class)

package com.uandcode.effects.core.testing.ksp

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import java.io.File

public class KspResult(
    public val origin: JvmCompilationResult
) {

    public val generatedKspFiles: List<OutputFile> by lazy {
        val parentKspDir = File(origin.outputDirectory.parentFile, "ksp/sources/kotlin")
        val allFiles = parentKspDir.listRecursively()
        allFiles.map {
            OutputFile(
                content = it.readText(),
                name = it.toRelativeString(parentKspDir),
            )
        }
    }

    public val defaultEffectStoreName: String = "com/uandcode/effects/core/kspcontract/AnnotationBasedProxyEffectStore.kt"

    public fun assertGeneratedFileCount(count: Int) {
        assertEquals(
            "Unexpected count of generated files. Expected count = $count, actual count = ${generatedKspFiles.size}",
            count,
            generatedKspFiles.size,
        )
    }

    public fun assertGeneratedFile(
        name: String,
        @Language("kotlin") expectedContent: String
    ) {
        val outputFile = generatedKspFiles.firstOrNull { it.name == name }
        if (outputFile == null) {
            fail("File '$name' does not exist")
        } else {
            val expectedContentWithoutSpaces = expectedContent.removeSpaces()
            val actualContent = outputFile.content
            val actualContentWithoutSpaces = actualContent.removeSpaces()
            if (actualContentWithoutSpaces != expectedContentWithoutSpaces) {
                println("-------")
                println("Expected content of '$name':")
                println(expectedContent)
                println("-------")
                println("Actual content of '$name':")
                println(actualContent)
                fail("Generated file '$name' has non-expected content.")
            }
        }
    }

    public fun assertGeneratedFileDoesNotExist(name: String) {
        assertTrue(
            "File '$name' exists, but it is not expected",
            generatedKspFiles.all { it.name != name }
        )
    }

    public fun assertCompiled() {
        assertTrue(
            "Expected successful compilation, but actual code is ${origin.exitCode}",
            origin.exitCode == KotlinCompilation.ExitCode.OK,
        )
    }

    public fun assertCompileError(
        expectedMessage: String = ""
    ) {
        assertTrue(
            "Expected compilation exit code = COMPILATION_ERROR, but actual code is ${origin.exitCode}",
            origin.exitCode == KotlinCompilation.ExitCode.COMPILATION_ERROR,
        )
        assertTrue(
            "Expected error message not found: '$expectedMessage'",
            origin.messages.contains(expectedMessage)
        )
    }

    private fun String.removeSpaces(): String {
        return lines()
            .map { it.replace(" ", "") }
            .filter { it.isNotBlank() }
            .joinToString("")
    }

    private fun File.listRecursively(): List<File> {
        return if (isDirectory) {
            listFiles()
                ?.flatMap { it.listRecursively() }
                ?: emptyList()
        } else {
            listOf(this)
        }
    }

}
