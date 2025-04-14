package com.uandcode.effects.koin.compiler

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.compiler.common.api.AbstractMetadataGenerator
import com.uandcode.effects.compiler.common.api.EffectExtension
import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.GeneratedProxy
import com.uandcode.effects.compiler.common.api.data.GroupedMetadata
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.api.data.ParsedMetadata
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.koin.compiler.data.KoinParsedEffect
import com.uandcode.effects.koin.compiler.data.KoinParsedMetadata
import com.uandcode.effects.koin.compiler.data.KoinScope
import com.uandcode.effects.koin.compiler.exception.InconsistentScopesException
import com.uandcode.effects.koin.compiler.generators.KoinEffectExtensionGenerator

class KoinEffectExtension : EffectExtension {

    override val effectAnnotation: ClassName = Const.KoinEffectAnnotationName
    override val metadataAnnotation: ClassName = Const.KoinEffectMetadataAnnotationName

    override fun parseEffect(classDeclaration: KSClassDeclarationWrapper): ParsedEffect {
        return KoinParsedEffect(classDeclaration, effectAnnotation)
    }

    override fun buildMetadataFromAnnotation(
        interfaceDeclarations: List<KSClassDeclarationWrapper>,
        implementationClassDeclaration: KSClassDeclarationWrapper,
        metadataDeclaration: KSClassDeclaration,
        metadataAnnotation: KSAnnotationWrapper,
    ): ParsedMetadata {
        return KoinParsedMetadata(
            interfaceDeclarations = interfaceDeclarations,
            implementationClassDeclaration = implementationClassDeclaration,
            metadataDeclaration = metadataDeclaration,
            koinScope = KoinScope.fromMetadataAnnotation(metadataAnnotation)
        )
    }

    override fun buildMetadataFromParsedEffect(
        parsedEffect: ParsedEffect,
    ): ParsedMetadata {
        return KoinParsedMetadata(parsedEffect as KoinParsedEffect)
    }

    override fun validateEffects(parsedEffects: Sequence<ParsedEffect>) = Unit

    override fun validateMetadata(groupedMetadata: Map<ClassName, List<ParsedMetadata>>) {
        groupedMetadata.forEach {  (interfaceName, effectMetadataList) ->
            val firstEffect = effectMetadataList.first() as KoinParsedMetadata
            effectMetadataList.forEach { currentEffect ->
                currentEffect as KoinParsedMetadata
                if (currentEffect.koinScope != firstEffect.koinScope) {
                    val interfaceDeclaration = firstEffect.interfaceDeclarations.first { it.toClassName() == interfaceName }
                    throw InconsistentScopesException(
                        interfaceDeclaration,
                        firstEffect.implementationClassName,
                        firstEffect.koinScope,
                        currentEffect.implementationClassName,
                        currentEffect.koinScope,
                    )
                }
            }
        }
    }

    override fun metadataGenerator(writer: KspClassWriter): AbstractMetadataGenerator {
        return KoinMetadataGenerator(writer)
    }

    override fun generateExtensions(
        groupedMetadata: GroupedMetadata,
        generatedProxies: List<GeneratedProxy>,
        writer: KspClassWriter
    ) {
        KoinEffectExtensionGenerator(writer).generate(groupedMetadata, generatedProxies)
    }

}
