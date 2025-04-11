package com.uandcode.effects.compiler.common.api

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.uandcode.effects.compiler.common.InternalSymbolProcessor

/**
 * Main entry point for the KSP processor.
 *
 * You can instantiate this class in your own KSP processor to extend the functionality of the
 * core effect compiler.
 *
 * Usage example:
 *
 * ```
 * class MySymbolProcessorProvider : SymbolProcessorProvider {
 *     override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
 *         return EffectSymbolProcessor(
 *             environment = environment,
 *             effectExtension = // your own extension here,
 *         )
 *     }
 * }
 * ```
 *
 * @see EffectExtension
 *
 * @param effectExtension the [EffectExtension] instance to be used for processing sources
 *                        and generating additional code at compile time.
 */
public class EffectSymbolProcessor(
    environment: SymbolProcessorEnvironment,
    effectExtension: EffectExtension,
) : SymbolProcessor {

    private val internalSymbolProcessor = InternalSymbolProcessor(
        logger = environment.logger,
        codeGenerator = environment.codeGenerator,
        effectExtension = effectExtension,
        processingMode = ProcessingMode.fromOptions(environment.options),
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        return internalSymbolProcessor.process(resolver)
    }

}
