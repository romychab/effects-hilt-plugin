package com.uandcode.effects.koin.compiler.generators

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.GeneratedProxy
import com.uandcode.effects.compiler.common.api.data.GroupedMetadata
import com.uandcode.effects.compiler.common.api.data.aggregateDependencies
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.koin.compiler.Const
import com.uandcode.effects.koin.compiler.data.KoinParsedMetadata
import com.uandcode.effects.koin.compiler.data.KoinScope

class KoinEffectExtensionGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(
        groupedMetadata: GroupedMetadata,
        generatedProxies: List<GeneratedProxy>,
    ) {
        val typeSpec = TypeSpec.objectBuilder(Const.GeneratedKoinExtensionObjectClassName)
            .addModifiers(KModifier.PUBLIC)
            .addInstallGeneratedEffectsMethod(groupedMetadata)
            .build()

        writer.write(
            typeSpec = typeSpec,
            dependencies = generatedProxies.aggregateDependencies(),
            pkg = Const.GeneratedKoinExtensionObjectClassName.packageName,
            builder = { addImports() }
        )
    }

    private fun FileSpec.Builder.addImports() {
        addImport("org.koin.dsl", "module")
        addImport("org.koin.core.qualifier", "named")
        addImport("com.uandcode.effects.core", "ManagedInterfaces")
        addImport("com.uandcode.effects.koin", "effect", "effectScope")
        addImport(
            "com.uandcode.effects.koin.internal",
            "internalSetupRootEffects", "internalSetupScopedEffects",
        )
    }

    private fun TypeSpec.Builder.addInstallGeneratedEffectsMethod(
        groupedMetadata: GroupedMetadata,
    ) = apply {
        val map = mutableMapOf<KoinScope, MutableList<InterfaceRecord>>()
        groupedMetadata.forEach { interfaceDeclaration, parsedMetadata ->
            parsedMetadata as List<KoinParsedMetadata>
            val list = map.computeIfAbsent(parsedMetadata.first().koinScope) { mutableListOf() }
            list.add(InterfaceRecord(interfaceDeclaration, parsedMetadata))
        }
        addFunction(
            FunSpec.builder("installGeneratedEffects")
                .addModifiers(KModifier.PUBLIC)
                .addParameter("application", Const.KoinApplicationClassName)
                .addCode(CodeBlock.builder()
                    .beginControlFlow("val module = module {")
                    .addStatement("internalSetupRootEffects()")
                    .registerAllEffects(map)
                    .endControlFlow()
                    .addStatement("application.modules(module)")
                    .build())
                .build()
        )
    }

    private fun CodeBlock.Builder.registerAllEffects(
        map: Map<KoinScope, MutableList<InterfaceRecord>>
    ) = apply {
        map.forEach { (koinScope, records) ->
            when (koinScope) {
                KoinScope.Empty -> registerInterfaces(records)
                is KoinScope.Class -> {
                    beginControlFlow("effectScope<%T> {", koinScope.className)
                    registerInterfaces(records)
                    endControlFlow()
                }
                is KoinScope.Named -> {
                    beginControlFlow("effectScope(named(%S)) {", koinScope.name)
                    registerInterfaces(records)
                    endControlFlow()
                }
            }
        }
    }

    private fun CodeBlock.Builder.registerInterfaces(
        records: List<InterfaceRecord>
    ) = apply {
        records.forEach { record ->
            val interfaceClassName = record.interfaceDeclaration.toClassName()
            addStatement("effect<%T>()", interfaceClassName)
        }
    }

    private class InterfaceRecord(
        val interfaceDeclaration: KSClassDeclarationWrapper,
        val implementations: List<KoinParsedMetadata>,
    )

}
