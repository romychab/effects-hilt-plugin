package com.uandcode.effects.core.compiler.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import com.uandcode.effects.core.compiler.api.EffectExtension
import com.uandcode.effects.core.compiler.api.data.GeneratedProxy
import com.uandcode.effects.core.compiler.api.data.ParsedMetadata
import com.uandcode.effects.core.compiler.Const

internal class ProxyEffectStoreGenerator(
    private val codeGenerator: CodeGenerator,
) {

    fun generate(
        extension: EffectExtension,
        generatedProxies: List<GeneratedProxy>,
        groupedMetadata: Map<ClassName, List<ParsedMetadata>>,
    ) {
        val fileSpec = FileSpec.builder(Const.GeneratedProxyEffectStoreClassName)
            .addInstanceProperty(extension, generatedProxies, groupedMetadata)
            .addType(
                TypeSpec.objectBuilder(Const.GeneratedProxyEffectStoreClassName)
                    .addSuperinterface(
                        superinterface = Const.ProxyEffectStoreInterfaceClassName,
                        delegate = CodeBlock.of(INSTANCE_PROPERTY)
                    )
                    .build()
            )
            .build()

        fileSpec.writeTo(
            codeGenerator = codeGenerator,
            dependencies = Dependencies(
                aggregating = false,
                sources = generatedProxies.flatMap { it.dependencies }.toTypedArray()
            )
        )
    }

    private fun FileSpec.Builder.addInstanceProperty(
        extension: EffectExtension,
        generatedProxies: List<GeneratedProxy>,
        groupedMetadata: Map<ClassName, List<ParsedMetadata>>,
    ) = apply {
        addProperty(
            PropertySpec.builder(INSTANCE_PROPERTY, Const.InternalProxyEffectStoreImplementationClassName)
                .addModifiers(KModifier.PRIVATE)
                .initializer(
                    CodeBlock.builder()
                        .addInitializer(extension)
                        .apply {
                            generatedProxies.forEach { result ->
                                add("registerProxyProvider(%T::class, ::%T)\n", result.interfaceClassName, result.proxyClassName)
                            }
                            groupedMetadata.forEach { (_, parsedMetadataList) ->
                                parsedMetadataList.forEach { parsedMetadata ->
                                    add("registerTarget(%T::class, %T::class)\n", parsedMetadata.implementationClassName, parsedMetadata.interfaceClassName)
                                }
                            }
                        }
                        .endControlFlow()
                        .build()
                )
                .build()
        )
    }

    private fun CodeBlock.Builder.addInitializer(
        extension: EffectExtension,
    ) = apply {
        val applicationAnnotation = "@${extension.applicationAnnotation.simpleName}"
        val effectAnnotation = "@${extension.effectAnnotation.simpleName}"
        beginControlFlow(
            controlFlow = "%T(%T.Default(%S, %S)).apply {",
            Const.InternalProxyEffectStoreImplementationClassName,
            Const.ProxyConfigurationClassName,
            applicationAnnotation,
            effectAnnotation
        )
    }

    private companion object {
        const val INSTANCE_PROPERTY = "instance"
    }

}
