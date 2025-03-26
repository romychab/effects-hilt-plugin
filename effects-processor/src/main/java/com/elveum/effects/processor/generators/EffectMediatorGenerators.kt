package com.elveum.effects.processor.generators

import com.elveum.effects.processor.data.EffectMetadata
import com.elveum.effects.processor.generators.base.KspClassWriter

class EffectMediatorGenerators(
    writer: KspClassWriter,
) {

    private val mediatorGenerator = EffectMediatorGenerator(writer)
    private val interfaceHiltModuleGenerator = EffectInterfaceHiltModuleGenerator(writer)
    private val viewModelMediatorGenerator = EffectViewModelMediatorGenerator(writer)
    private val viewModelMediatorHiltModuleGenerator = EffectViewModelMediatorHiltModuleGenerator(writer)

    fun generateEffectMediators(uniqueEffectMetadataList: List<EffectMetadata>) {
        uniqueEffectMetadataList.forEach(::generateEffectMediator)
    }

    private fun generateEffectMediator(parsedMetadata: EffectMetadata) {
        val mediatorResult = mediatorGenerator.generate(parsedMetadata)
        interfaceHiltModuleGenerator.generate(parsedMetadata, mediatorResult)
        if (parsedMetadata.shouldGenerateViewModelMediator()) {
            val viewModelMediatorResult = viewModelMediatorGenerator.generate(
                parsedMetadata, mediatorResult
            )
            viewModelMediatorHiltModuleGenerator.generate(
                parsedMetadata, mediatorResult, viewModelMediatorResult
            )
        }
    }

}
