package com.elveum.effects.processor.v2

import com.elveum.effects.processor.v2.generators.EffectMediatorGenerator
import com.elveum.effects.processor.v2.generators.KspClassV2Writer
import com.elveum.effects.processor.v2.parser.parseEffects
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

/// !!!! TODO: process qualifier annotations if an effect interface has more than
//             one implementation
class EffectSymbolProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            val effects = parseEffects(resolver)
            validateEffects(effects)
            val writer = KspClassV2Writer(codeGenerator)
            val mediatorGenerator = EffectMediatorGenerator(logger, writer)
            effects.forEach { effectInfo ->
                mediatorGenerator.generate(effectInfo)
            }
        } catch (e: EffectKspException) {
            logger.error(e.message ?: "Failed to process effect annotations", e.node)
        }
        return emptyList()
    }

}
