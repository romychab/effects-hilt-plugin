package com.elveum.effects.processor.generators

import com.elveum.effects.processor.data.EffectMetadata
import com.elveum.effects.processor.extensions.KSClassDeclarationWrapper
import com.elveum.effects.processor.generators.base.KspClassWriter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

class EffectMediatorGenerators(
    writer: KspClassWriter,
) {

    private val mediatorGenerator = EffectMediatorGenerator(writer)
    private val interfaceHiltModuleGenerator = EffectInterfaceHiltModuleGenerator(writer)
    private val viewModelMediatorGenerator = EffectViewModelMediatorGenerator(writer)
    private val viewModelMediatorHiltModuleGenerator = EffectViewModelMediatorHiltModuleGenerator(writer)

    fun generateEffectMediators(interfaceToImplementationMap: Map<ClassName, List<EffectMetadata>>) {
        interfaceToImplementationMap.forEach { (interfaceClassName, metadata) ->
            val interfaceDeclaration = metadata
                .first()
                .targetInterfaceList
                .first { it.toClassName() == interfaceClassName }
            generateEffectMediator(interfaceDeclaration, metadata.first())
        }
    }

    private fun generateEffectMediator(
        interfaceDeclaration: KSClassDeclarationWrapper,
        parsedMetadata: EffectMetadata,
    ) {
        val mediatorResult = mediatorGenerator.generate(interfaceDeclaration, parsedMetadata)
        val interfaceClassName = interfaceDeclaration.toClassName()
        interfaceHiltModuleGenerator.generate(interfaceClassName, parsedMetadata, mediatorResult)
        if (parsedMetadata.shouldGenerateViewModelMediator()) {
            val viewModelMediatorResult = viewModelMediatorGenerator.generate(
                interfaceClassName, parsedMetadata, mediatorResult
            )
            viewModelMediatorHiltModuleGenerator.generate(
                interfaceClassName, parsedMetadata, mediatorResult, viewModelMediatorResult
            )
        }
    }

}
