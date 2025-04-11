package com.uandcode.effects.compiler.common.generators

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import com.uandcode.effects.compiler.common.Const
import com.uandcode.effects.compiler.common.api.EffectExtension
import com.uandcode.effects.compiler.common.api.data.GeneratedProxy
import com.uandcode.effects.compiler.common.api.data.GroupedMetadata
import com.uandcode.effects.compiler.common.api.data.aggregateDependencies

internal class ProxyEffectStoreGenerator(
    private val codeGenerator: CodeGenerator,
) {

    fun generate(
        extension: EffectExtension,
        validationResult: GroupedMetadata,
        generatedProxies: List<GeneratedProxy>,
    ) {
        val fileSpec = FileSpec.builder(Const.GeneratedProxyEffectStoreClassName)
            .addInstanceProperty(extension, generatedProxies, validationResult)
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
            dependencies = generatedProxies.aggregateDependencies(),
        )
    }

    private fun FileSpec.Builder.addInstanceProperty(
        extension: EffectExtension,
        generatedProxies: List<GeneratedProxy>,
        validationResult: GroupedMetadata,
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
                            validationResult.effectPairs.forEach {
                                add("registerTarget(%T::class, %T::class)\n", it.implementationClass, it.interfaceClass)
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
        val effectAnnotation = "@${extension.effectAnnotation.simpleName}"
        beginControlFlow(
            controlFlow = "%T(%T.Default(%S)).apply {",
            Const.InternalProxyEffectStoreImplementationClassName,
            Const.ProxyConfigurationClassName,
            effectAnnotation
        )
    }

    private companion object {
        const val INSTANCE_PROPERTY = "instance"
    }

}
