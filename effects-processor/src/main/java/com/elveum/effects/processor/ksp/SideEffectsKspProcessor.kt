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

class SideEffectsKspProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        try {
            val annotatedClasses = resolver
                .getSymbolsWithAnnotation(KspNames.sideEffectAnnotationName)
                .filterIsInstance<KSClassDeclaration>()

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

}
