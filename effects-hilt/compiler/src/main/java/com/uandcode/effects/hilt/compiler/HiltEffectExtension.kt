@file:OptIn(KspExperimental::class)

package com.uandcode.effects.hilt.compiler

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.compiler.common.api.AbstractMetadataGenerator
import com.uandcode.effects.compiler.common.api.EffectExtension
import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.GroupedMetadata
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.api.data.ParsedMetadata
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.hilt.compiler.data.HiltParsedEffect
import com.uandcode.effects.hilt.compiler.data.HiltParsedMetadata
import com.uandcode.effects.hilt.compiler.data.SupportedHiltComponent
import com.uandcode.effects.hilt.compiler.exceptions.InconsistentHiltComponentsException
import com.uandcode.effects.hilt.compiler.generators.HiltImplementationModuleGenerator
import com.uandcode.effects.hilt.compiler.generators.HiltInterfaceModuleGenerator
import com.uandcode.effects.hilt.compiler.generators.HiltMetadataGenerator

class HiltEffectExtension : EffectExtension {

    override val effectAnnotation: ClassName = Const.HiltEffectAnnotationName
    override val metadataAnnotation: ClassName = Const.HiltEffectMetadataAnnotationName

    override fun parseEffect(classDeclaration: KSClassDeclarationWrapper): ParsedEffect {
        return HiltParsedEffect(classDeclaration)
    }

    override fun buildMetadataFromAnnotation(
        interfaceDeclarations: List<KSClassDeclarationWrapper>,
        implementationClassDeclaration: KSClassDeclarationWrapper,
        metadataDeclaration: KSClassDeclaration,
        metadataAnnotation: KSAnnotationWrapper,
    ): ParsedMetadata {
        val hiltComponent = ClassName.bestGuess(metadataAnnotation.getString(Const.MetadataHiltComponent))
        val annotation = implementationClassDeclaration.wrappedAnnotations
            .first { it.isInstanceOf(Const.HiltEffectAnnotationName) }
        return HiltParsedMetadata(
            interfaceDeclarations = interfaceDeclarations,
            implementationClassDeclaration = implementationClassDeclaration,
            metadataDeclaration = metadataDeclaration,
            hiltComponent = SupportedHiltComponent.fromClassName(hiltComponent, annotation),
        )
    }

    override fun buildMetadataFromParsedEffect(
        parsedEffect: ParsedEffect,
    ): ParsedMetadata {
        val hiltParsedEffect = parsedEffect as HiltParsedEffect
        return HiltParsedMetadata(hiltParsedEffect)
    }

    override fun validateEffects(parsedEffects: Sequence<ParsedEffect>) = Unit

    override fun validateMetadata(groupedMetadata: Map<ClassName, List<ParsedMetadata>>) {
        groupedMetadata.forEach {  (interfaceName, effectMetadataList) ->
            val firstEffect = effectMetadataList.first() as HiltParsedMetadata
            effectMetadataList.forEach { currentEffect ->
                currentEffect as HiltParsedMetadata
                if (currentEffect.hiltComponent != firstEffect.hiltComponent) {
                    val interfaceDeclaration = firstEffect.interfaceDeclarations.first { it.toClassName() == interfaceName }
                    throw InconsistentHiltComponentsException(
                        interfaceDeclaration,
                        firstEffect.implementationClassName,
                        firstEffect.hiltComponent.componentName,
                        currentEffect.implementationClassName,
                        currentEffect.hiltComponent.componentName,
                    )
                }
            }
        }
    }

    override fun metadataGenerator(writer: KspClassWriter): AbstractMetadataGenerator {
        return HiltMetadataGenerator(writer)
    }

    @Suppress("UNCHECKED_CAST")
    override fun generateExtensions(
        groupedMetadata: GroupedMetadata,
        writer: KspClassWriter
    ) {
        val interfaceModuleGenerator = HiltInterfaceModuleGenerator(writer)
        val implementationModuleGenerator = HiltImplementationModuleGenerator(writer)

        val generatedImplModules = mutableSetOf<ClassName>()
        groupedMetadata.forEach { interfaceDeclaration, metadataList ->
            metadataList as List<HiltParsedMetadata>
            interfaceModuleGenerator.generate(interfaceDeclaration, metadataList.first())
            metadataList.forEach { metadata ->
                if (!generatedImplModules.contains(metadata.implementationClassName)) {
                    implementationModuleGenerator.generate(metadata)
                    generatedImplModules.add(metadata.implementationClassName)
                }
            }
        }
    }

}
