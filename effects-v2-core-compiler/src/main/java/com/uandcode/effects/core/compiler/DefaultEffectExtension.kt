package com.uandcode.effects.core.compiler

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.uandcode.effects.core.annotations.EffectApplication
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.effects.core.annotations.EffectMetadata
import com.uandcode.effects.core.compiler.api.AbstractMetadataGenerator
import com.uandcode.effects.core.compiler.api.EffectExtension
import com.uandcode.effects.core.compiler.api.KspClassWriter
import com.uandcode.effects.core.compiler.api.data.EffectCleanUpMethodName
import com.uandcode.effects.core.compiler.api.data.ParsedEffect
import com.uandcode.effects.core.compiler.api.data.ParsedMetadata
import com.uandcode.effects.core.compiler.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.core.compiler.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.core.compiler.generators.DefaultMetadataGenerator

internal object DefaultEffectExtension : EffectExtension {

    override val applicationAnnotation: ClassName = EffectApplication::class.asClassName()
    override val effectAnnotation: ClassName = EffectClass::class.asClassName()
    override val metadataAnnotation: ClassName = EffectMetadata::class.asClassName()

    override fun parseEffect(classDeclaration: KSClassDeclarationWrapper): ParsedEffect {
        return ParsedEffect(classDeclaration)
    }

    override fun buildMetadataFromAnnotation(
        applicationClassDeclaration: KSClassDeclaration,
        interfaceDeclaration: KSClassDeclarationWrapper,
        implementationClassDeclaration: KSClassDeclarationWrapper,
        cleanUpMethodName: EffectCleanUpMethodName,
        metadataDeclaration: KSClassDeclaration,
        metadataAnnotation: KSAnnotationWrapper
    ): ParsedMetadata {
        return ParsedMetadata(
            applicationClassDeclaration = applicationClassDeclaration,
            interfaceDeclaration = interfaceDeclaration,
            implementationClassDeclaration = implementationClassDeclaration,
            cleanUpMethodName = cleanUpMethodName,
            metadataDeclaration = metadataDeclaration
        )
    }

    override fun buildMetadataFromParsedEffect(
        parsedEffect: ParsedEffect,
        applicationClassDeclaration: KSClassDeclaration
    ): ParsedMetadata {
        return ParsedMetadata(parsedEffect, applicationClassDeclaration)
    }

    override fun validateEffects(parsedEffects: Sequence<ParsedEffect>) = Unit

    override fun metadataGenerator(writer: KspClassWriter): AbstractMetadataGenerator {
        return DefaultMetadataGenerator(writer)
    }

    override fun validateMetadata(
        groupedMetadata: Map<ClassName, List<ParsedMetadata>>
    ) = Unit

    override fun generateExtensions(
        groupedMetadata: Map<ClassName, List<ParsedMetadata>>,
        writer: KspClassWriter
    ) = Unit

}
