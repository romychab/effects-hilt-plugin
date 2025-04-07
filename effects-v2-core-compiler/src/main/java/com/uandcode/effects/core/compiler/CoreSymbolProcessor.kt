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
import com.uandcode.effects.core.compiler.api.data.GeneratedProxy
import com.uandcode.effects.core.compiler.api.data.ParsedEffect
import com.uandcode.effects.core.compiler.api.data.ParsedMetadata
import com.uandcode.effects.core.compiler.exceptions.InternalEffectKspException
import com.uandcode.effects.core.compiler.generators.ProxyEffectGenerator
import com.uandcode.effects.core.compiler.generators.ProxyEffectStoreGenerator
import com.uandcode.effects.core.compiler.parser.parseEffects
import com.uandcode.effects.core.compiler.parser.parseMetadata
import com.uandcode.effects.core.compiler.validators.validateAndFilterEffectMetadata
import com.uandcode.effects.core.compiler.validators.validateEffects

internal class CoreSymbolProcessor(
    private val logger: KSPLogger,
    codeGenerator: CodeGenerator,
    private val effectExtension: EffectExtension = DefaultEffectExtension,
) : SymbolProcessor {

    private val writer = KspClassWriterImpl(codeGenerator)
    private val metadataGenerator = effectExtension.metadataGenerator(writer)
    private val proxyEffectGenerator = ProxyEffectGenerator(writer)
    private val proxyEffectStoreGenerator = ProxyEffectStoreGenerator(codeGenerator)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            val appClassDeclaration = getApplication(resolver)

            val effects = parseEffects(resolver, appClassDeclaration, effectExtension)
            validateEffects(effects)
            effectExtension.validateEffects(effects)

            generateMetadata(effects)

            if (appClassDeclaration != null) {
                val parsedMetadataList = parseMetadata(resolver, effectExtension, appClassDeclaration) +
                        effects.map { effectExtension.buildMetadataFromParsedEffect(it, appClassDeclaration) }
                val validationResult = validateAndFilterEffectMetadata(effectExtension, parsedMetadataList)
                val groupedMetadata = validationResult.groupedMetadata
                val autoCloseableDeclaration = resolver.getClassDeclarationByName(Const.AutoCloseableClassName.canonicalName)
                    ?: throw InternalEffectKspException("Can't find AutoCloseable interface in the classpath.")
                val generatedProxies = generateProxies(validationResult.uniqueMetadata, autoCloseableDeclaration)
                proxyEffectStoreGenerator.generate(effectExtension, generatedProxies, groupedMetadata)
                effectExtension.generateExtensions(groupedMetadata, writer)
            }
        } catch (e: AbstractEffectKspException) {
            logger.error(e.message ?: "Failed to process effect annotations", e.node)
        }
        return emptyList()
    }

    private fun generateMetadata(effects: Sequence<ParsedEffect>) {
        effects.forEach { metadataGenerator.generate(it) }
    }

    private fun generateProxies(
        metadataList: List<ParsedMetadata>,
        autoCloseableDeclaration: KSClassDeclaration,
    ): List<GeneratedProxy> {
        return metadataList.map { proxyEffectGenerator.generate(it, autoCloseableDeclaration) }
    }

    private fun getApplication(resolver: Resolver): KSClassDeclaration? {
        val annotatedSymbols = resolver.getSymbolsWithAnnotation(
            effectExtension.applicationAnnotation.canonicalName
        )
        return annotatedSymbols.firstOrNull() as? KSClassDeclaration
    }

}
