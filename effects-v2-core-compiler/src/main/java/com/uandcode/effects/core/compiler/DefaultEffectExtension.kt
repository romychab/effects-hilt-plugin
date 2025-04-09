package com.uandcode.effects.core.compiler

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.effects.core.annotations.EffectMetadata
import com.uandcode.effects.core.compiler.api.AbstractMetadataGenerator
import com.uandcode.effects.core.compiler.api.EffectExtension
import com.uandcode.effects.core.compiler.api.KspClassWriter
import com.uandcode.effects.core.compiler.api.ProcessingMode
import com.uandcode.effects.core.compiler.api.data.ParsedEffect
import com.uandcode.effects.core.compiler.api.data.ParsedMetadata
import com.uandcode.effects.core.compiler.api.data.GroupedMetadata
import com.uandcode.effects.core.compiler.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.core.compiler.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.core.compiler.generators.DefaultMetadataGenerator

internal object DefaultEffectExtension : EffectExtension {

    override val effectAnnotation: ClassName = EffectClass::class.asClassName()
    override val metadataAnnotation: ClassName = EffectMetadata::class.asClassName()

    override fun parseEffect(
        classDeclaration: KSClassDeclarationWrapper,
    ): ParsedEffect {
        return ParsedEffect(classDeclaration)
    }

    override fun buildMetadataFromAnnotation(
        interfaceDeclarations: List<KSClassDeclarationWrapper>,
        implementationClassDeclaration: KSClassDeclarationWrapper,
        metadataDeclaration: KSClassDeclaration,
        metadataAnnotation: KSAnnotationWrapper
    ): ParsedMetadata {
        return ParsedMetadata(
            interfaceDeclarations = interfaceDeclarations,
            implementationClassDeclaration = implementationClassDeclaration,
            metadataDeclaration = metadataDeclaration
        )
    }

    override fun buildMetadataFromParsedEffect(
        parsedEffect: ParsedEffect,
    ): ParsedMetadata {
        return ParsedMetadata(parsedEffect)
    }

    override fun validateEffects(parsedEffects: Sequence<ParsedEffect>) = Unit

    override fun metadataGenerator(writer: KspClassWriter): AbstractMetadataGenerator {
        return DefaultMetadataGenerator(writer)
    }

    override fun validateMetadata(
        groupedMetadata: Map<ClassName, List<ParsedMetadata>>
    ) = Unit

    override fun generateExtensions(
        processingMode: ProcessingMode,
        groupedMetadata: GroupedMetadata,
        writer: KspClassWriter
    ) = Unit

}
