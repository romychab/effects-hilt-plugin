package com.elveum.effects.processor.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class SideEffectsKspProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return SideEffectsKspProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
    }
}
