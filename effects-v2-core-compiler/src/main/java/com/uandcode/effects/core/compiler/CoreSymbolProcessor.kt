@file:OptIn(KspExperimental::class)

package com.uandcode.effects.core.compiler

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.uandcode.effects.core.compiler.api.AbstractEffectKspException
import com.uandcode.effects.core.compiler.api.EffectExtension
import com.uandcode.effects.core.compiler.api.ProcessingMode
import com.uandcode.effects.core.compiler.api.data.GeneratedProxy
import com.uandcode.effects.core.compiler.api.data.ParsedEffect
import com.uandcode.effects.core.compiler.api.data.GroupedMetadata
import com.uandcode.effects.core.compiler.exceptions.InternalEffectKspException
import com.uandcode.effects.core.compiler.generators.ProxyEffectGenerator
import com.uandcode.effects.core.compiler.generators.ProxyEffectStoreGenerator
import com.uandcode.effects.core.compiler.parser.parseEffects
import com.uandcode.effects.core.compiler.parser.parseMetadata
import com.uandcode.effects.core.compiler.validators.validateAndFilterEffectMetadata
import com.uandcode.effects.core.compiler.validators.validateEffects

internal class CoreSymbolProcessor(
    private val logger: KSPLogger,
    private val processingMode: ProcessingMode,
    codeGenerator: CodeGenerator,
    private val effectExtension: EffectExtension = DefaultEffectExtension,
) : SymbolProcessor {

    private val writer = KspClassWriterImpl(codeGenerator)
    private val metadataGenerator = effectExtension.metadataGenerator(writer)
    private val proxyEffectGenerator = ProxyEffectGenerator(writer)
    private val proxyEffectStoreGenerator = ProxyEffectStoreGenerator(codeGenerator)

    private var isMetadataFromOtherModulesProcessed = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            val effects = parseEffects(resolver, effectExtension)
            validateEffects(effects)
            effectExtension.validateEffects(effects)
            when (processingMode) {
                ProcessingMode.GenerateMetadata -> generateMetadata(effects)
                ProcessingMode.AggregateMetadata -> aggregateMetadata(resolver, effects)
            }
        } catch (e: AbstractEffectKspException) {
            logger.error(e.message ?: "Failed to process effect annotations", e.node)
        }
        return emptyList()
    }

    private fun generateMetadata(effects: Sequence<ParsedEffect>) {
        effects.forEach { metadataGenerator.generate(it) }
    }

    private fun aggregateMetadata(
        resolver: Resolver,
        effects: Sequence<ParsedEffect>,
    ) {
        val metadataFromOtherModules = parseMetadata(resolver, effectExtension)
        val metadataFromThisModule = effects.map(effectExtension::buildMetadataFromParsedEffect).toList()
        val mergedMetadataList = metadataFromThisModule + if (isMetadataFromOtherModulesProcessed) {
            emptyList()
        } else {
            metadataFromOtherModules
        }
        val validatedMetadata = validateAndFilterEffectMetadata(effectExtension, mergedMetadataList)
        val autoCloseableDeclaration = resolver.getClassDeclarationByName(Const.AutoCloseableClassName.canonicalName)
            ?: throw InternalEffectKspException("Can't find AutoCloseable interface in the classpath.")
        val generatedProxies = generateProxies(validatedMetadata, autoCloseableDeclaration)
        if (!isMetadataFromOtherModulesProcessed || metadataFromThisModule.isNotEmpty()) {
            proxyEffectStoreGenerator.generate(effectExtension, validatedMetadata, generatedProxies)
        }
        effectExtension.generateExtensions(ProcessingMode.AggregateMetadata, validatedMetadata, writer)
        isMetadataFromOtherModulesProcessed = true
    }

    private fun generateProxies(
        validationResult: GroupedMetadata,
        autoCloseableDeclaration: KSClassDeclaration,
    ): List<GeneratedProxy> {
        val generatedProxyList = mutableListOf<GeneratedProxy>()
        validationResult.forEach { interfaceDeclaration, parsedMetadata ->
            val generatedProxy = proxyEffectGenerator.generate(interfaceDeclaration, parsedMetadata, autoCloseableDeclaration)
            generatedProxyList.add(generatedProxy)
        }
        return generatedProxyList
    }

}
