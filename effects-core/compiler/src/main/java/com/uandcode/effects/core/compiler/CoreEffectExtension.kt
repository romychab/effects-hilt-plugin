package com.uandcode.effects.core.compiler

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.uandcode.effects.compiler.common.api.AbstractMetadataGenerator
import com.uandcode.effects.compiler.common.api.EffectExtension
import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.GroupedMetadata
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.api.data.ParsedMetadata
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.effects.core.annotations.EffectMetadata

internal object CoreEffectExtension : EffectExtension {

    override val effectAnnotation: ClassName = EffectClass::class.asClassName()
    override val metadataAnnotation: ClassName = EffectMetadata::class.asClassName()

    override fun parseEffect(
        classDeclaration: KSClassDeclarationWrapper,
    ): ParsedEffect {
        return ParsedEffect(classDeclaration, effectAnnotation)
    }

    override fun buildMetadataFromAnnotation(
        interfaceDeclarations: List<KSClassDeclarationWrapper>,
        implementationClassDeclaration: KSClassDeclarationWrapper,
        metadataDeclaration: KSClassDeclaration,
        metadataAnnotation: KSAnnotationWrapper,
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
        return CoreMetadataGenerator(writer)
    }

    override fun validateMetadata(
        groupedMetadata: Map<ClassName, List<ParsedMetadata>>
    ) = Unit

    override fun generateExtensions(
        groupedMetadata: GroupedMetadata,
        writer: KspClassWriter,
    ) = Unit

}
