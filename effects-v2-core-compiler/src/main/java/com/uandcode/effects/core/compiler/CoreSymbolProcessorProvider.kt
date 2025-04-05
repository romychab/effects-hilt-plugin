package com.uandcode.effects.core.compiler

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

public class CoreSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return CoreSymbolProcessor(
            environment.logger,
            environment.codeGenerator,
        )
    }

}
