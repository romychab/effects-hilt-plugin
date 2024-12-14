package com.elveum.effects.processor.ksp

import com.elveum.effects.processor.ksp.generators.KspImplementationModuleGenerator
import com.elveum.effects.processor.ksp.generators.KspMediatorGenerator
import com.elveum.effects.processor.ksp.generators.KspMediatorModuleGenerator
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class MviEffectsKspProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            val deprecatedAnnotatedClasses = findAnnotatedClasses(resolver, KspNames.deprecatedAnnotationName)
            val newAnnotatedClasses = findAnnotatedClasses(resolver, KspNames.mviEffectAnnotationName)
            val annotatedClasses = deprecatedAnnotatedClasses + newAnnotatedClasses

            val kspMediatorGenerator = KspMediatorGenerator(codeGenerator)
            val kspMediatorModuleGenerator = KspMediatorModuleGenerator()
            val kspImplementationModuleGenerator = KspImplementationModuleGenerator()

            annotatedClasses.forEach {  annotatedClass ->
                val result = kspMediatorGenerator.generateMediator(annotatedClass)
                val key = kspMediatorModuleGenerator.generateModule(result)
                kspImplementationModuleGenerator.generateModule(result.writer, result.parsedElements, key)
            }

        } catch (e: KspElementException) {
            logger.error(e.message ?: "Error", e.element)
        }
        return emptyList()
    }

    private fun findAnnotatedClasses(resolver: Resolver, annotationName: String): Sequence<KSClassDeclaration> {
        return resolver
            .getSymbolsWithAnnotation(annotationName)
            .filterIsInstance<KSClassDeclaration>()
    }

}
