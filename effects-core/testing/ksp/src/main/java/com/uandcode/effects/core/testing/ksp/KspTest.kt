@file:OptIn(ExperimentalCompilerApi::class)

package com.uandcode.effects.core.testing.ksp

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

public class KspTest(
    private val inputFiles: List<InputFile>,
    private val provider: SymbolProcessorProvider,
    private val options: Map<String, String> = emptyMap(),
) {

    public constructor(
        inputFile: InputFile,
        provider: SymbolProcessorProvider,
        options: Map<String, String> = emptyMap(),
    ) : this(listOf(inputFile), provider, options)

    public constructor(
        @Language("kotlin") inputContent: String,
        provider: SymbolProcessorProvider,
        options: Map<String, String> = emptyMap(),
    ) : this(InputFile(inputContent), provider, options)

    public fun compile(): KspResult {
        val compilation = KotlinCompilation().apply {
            sources = inputFiles.map { SourceFile.kotlin(it.name, it.content) }
            configureKsp(useKsp2 = true) {
                symbolProcessorProviders += provider
                processorOptions += options
            }
            messageOutputStream = System.out
            inheritClassPath = true
        }
        val result = compilation.compile()
        return KspResult(result)
    }

}
