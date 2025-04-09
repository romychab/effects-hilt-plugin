package com.elveum.effects.processor.generators

import com.elveum.effects.processor.data.Const
import com.elveum.effects.processor.data.EffectMetadata
import com.elveum.effects.processor.generators.base.KspClassWriter
import com.elveum.effects.processor.generators.base.TemplateBasedClassContent
import com.squareup.kotlinpoet.ClassName

class EffectViewModelMediatorHiltModuleGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(
        interfaceClassName: ClassName,
        effectMetadata: EffectMetadata,
        originMediatorResult: EffectMediatorGenerator.Result,
        viewModelMediatorResult: EffectViewModelMediatorGenerator.Result,
    ) {
        val hiltModuleName = "${interfaceClassName.simpleName}EffectViewModelModule"
        val classContent = TemplateBasedClassContent(
            templatePath = "EffectViewModelMediatorModuleTemplate.kt",
            pkg = interfaceClassName.packageName,
            className = hiltModuleName,
            dependencies = effectMetadata.dependencies,
        ).apply {
            setVariable("ORIGIN_MEDIATOR_NAME", originMediatorResult.mediatorClassName.simpleName)
            setVariable("VIEW_MODEL_MEDIATOR_NAME", viewModelMediatorResult.viewModelMediatorClassName.simpleName)
            setVariable("TARGET_INTERFACE_NAME", interfaceClassName.simpleName)
            setVariable("PRIORITY_CONST", Const.ViewModelPriorityConstName)
        }
        writer.write(classContent)
    }

}
