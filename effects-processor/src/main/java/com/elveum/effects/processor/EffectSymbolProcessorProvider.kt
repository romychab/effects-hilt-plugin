package com.elveum.effects.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class EffectSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EffectSymbolProcessor(
            logger = environment.logger,
            processingMode = ProcessingMode.fromOptions(environment.options),
            codeGenerator = environment.codeGenerator,
        )
    }

}
