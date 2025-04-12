package com.uandcode.effects.hilt.compiler

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.uandcode.effects.compiler.common.api.EffectSymbolProcessor

class HiltSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EffectSymbolProcessor(
            environment = environment,
            effectExtension = HiltEffectExtension(),
        )
    }

}
