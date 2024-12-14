package com.elveum.effects.processor.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class MviEffectsKspProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return MviEffectsKspProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
    }
}
