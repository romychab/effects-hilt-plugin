package com.elveum.effects.processor

import com.elveum.effects.processor.data.Const
import com.elveum.effects.processor.data.EffectInfo
import com.elveum.effects.processor.data.EffectMetadata
import com.elveum.effects.processor.exceptions.AbstractEffectKspException
import com.elveum.effects.processor.generators.EffectImplementationHiltModuleGenerator
import com.elveum.effects.processor.generators.EffectMediatorGenerators
import com.elveum.effects.processor.generators.EffectMetadataGenerator
import com.elveum.effects.processor.generators.base.KspClassWriter
import com.elveum.effects.processor.parser.parseEffects
import com.elveum.effects.processor.parser.parseMetadata
import com.elveum.effects.processor.validators.validateAndFilterEffectMetadata
import com.elveum.effects.processor.validators.validateEffects
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class EffectSymbolProcessor(
    private val logger: KSPLogger,
    codeGenerator: CodeGenerator,
) : SymbolProcessor {

    private val writer = KspClassWriter(codeGenerator)
    private val effectMediatorGenerators = EffectMediatorGenerators(writer)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            val effects = parseEffects(resolver)
            validateEffects(effects)

            generateEffectImplementationModules(effects)

            val hiltAppClassDeclaration = getHiltApp(resolver)
            if (hiltAppClassDeclaration != null) {
                val effectMetadataSequence = parseMetadata(resolver, hiltAppClassDeclaration) +
                        effects.map { EffectMetadata(it, hiltAppClassDeclaration) }
                val interfaceToImplementationMap = validateAndFilterEffectMetadata(effectMetadataSequence)
                effectMediatorGenerators.generateEffectMediators(interfaceToImplementationMap)
            }
        } catch (e: AbstractEffectKspException) {
            logger.error(e.message ?: "Failed to process effect annotations", e.node)
        }
        return emptyList()
    }

    private fun generateEffectImplementationModules(effects: Sequence<EffectInfo>) {
        val metadataGenerator = EffectMetadataGenerator(writer)
        val implHiltModuleGenerator = EffectImplementationHiltModuleGenerator(writer)
        effects.forEach { effectInfo ->
            metadataGenerator.generate(effectInfo)
            implHiltModuleGenerator.generate(effectInfo)
        }
    }

    private fun getHiltApp(resolver: Resolver): KSClassDeclaration? {
        val annotatedSymbols = resolver.getSymbolsWithAnnotation(
            Const.HiltAppAnnotationName.canonicalName
        )
        return annotatedSymbols.firstOrNull() as? KSClassDeclaration
    }

}
