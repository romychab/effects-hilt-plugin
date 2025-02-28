package com.elveum.effects.processor.v2

import com.elveum.effects.processor.v2.data.Const
import com.elveum.effects.processor.v2.data.EffectInfo
import com.elveum.effects.processor.v2.data.EffectMetadata
import com.elveum.effects.processor.v2.exceptions.AbstractEffectKspException
import com.elveum.effects.processor.v2.generators.EffectImplementationHiltModuleGenerator
import com.elveum.effects.processor.v2.generators.EffectInterfaceHiltModuleGenerator
import com.elveum.effects.processor.v2.generators.EffectMediatorGenerator
import com.elveum.effects.processor.v2.generators.EffectMetadataGenerator
import com.elveum.effects.processor.v2.generators.base.KspClassV2Writer
import com.elveum.effects.processor.v2.parser.parseEffects
import com.elveum.effects.processor.v2.parser.parseMetadata
import com.elveum.effects.processor.v2.validators.validateAndFilterEffectMetadata
import com.elveum.effects.processor.v2.validators.validateEffects
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

    private val writer = KspClassV2Writer(codeGenerator)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            val effects = parseEffects(resolver)
            validateEffects(effects)

            generateEffectImplementationModules(effects)

            val hiltAppClassDeclaration = getHiltApp(resolver)
            if (hiltAppClassDeclaration != null) {
                val effectMetadataSequence = parseMetadata(resolver, hiltAppClassDeclaration) +
                        effects.map { EffectMetadata(it, hiltAppClassDeclaration) }
                val uniqueEffectMetadataList = validateAndFilterEffectMetadata(effectMetadataSequence)
                generateEffectMediators(uniqueEffectMetadataList)
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

    private fun generateEffectMediators(
        uniqueEffectMetadataList: List<EffectMetadata>,
    ) {
        val mediatorGenerator = EffectMediatorGenerator(writer)
        val interfaceHiltModuleGenerator = EffectInterfaceHiltModuleGenerator(writer)
        uniqueEffectMetadataList.forEach { parsedMetadata ->
            val mediatorResult = mediatorGenerator.generate(parsedMetadata)
            interfaceHiltModuleGenerator.generate(parsedMetadata, mediatorResult)
        }
    }

    private fun getHiltApp(resolver: Resolver): KSClassDeclaration? {
        val annotatedSymbols = resolver.getSymbolsWithAnnotation(
            Const.HiltAppAnnotationName.canonicalName
        )
        return annotatedSymbols.firstOrNull() as? KSClassDeclaration
    }

}
