package com.uandcode.effects.core.testing.ksp

import com.google.devtools.ksp.processing.SymbolProcessorProvider

public abstract class AbstractKspTest {

    public abstract val symbolProcessorProvider: SymbolProcessorProvider

    public open val options: Map<String, String> = emptyMap()

    protected fun compile(
        source: String
    ): KspResult {
        return compile(InputFile(source))
    }

    protected fun compile(
        vararg sources: InputFile
    ): KspResult {
        return KspTest(
            inputFiles = sources.toList(),
            provider = symbolProcessorProvider,
            options = options,
        ).compile()
    }

}
