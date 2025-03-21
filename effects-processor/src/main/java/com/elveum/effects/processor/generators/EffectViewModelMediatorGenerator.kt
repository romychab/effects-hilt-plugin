package com.elveum.effects.processor.generators

import com.elveum.effects.processor.data.EffectMetadata
import com.elveum.effects.processor.generators.base.KspClassWriter
import com.elveum.effects.processor.generators.base.TemplateBasedClassContent
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

class EffectViewModelMediatorGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(
        effectMetadata: EffectMetadata,
        result: EffectMediatorGenerator.Result,
    ): Result {
        val interfaceDeclaration = effectMetadata.targetInterfaceDeclaration
        val interfaceClassName = interfaceDeclaration.toClassName()
        val viewModelMediatorName = "__${interfaceClassName.simpleName}ViewModelMediator"
        val classContent = TemplateBasedClassContent(
            templatePath = "EffectViewModelMediatorTemplate.kt",
            pkg = interfaceClassName.packageName,
            className = viewModelMediatorName,
            dependencies = effectMetadata.dependencies,
        ).apply {
            setVariable("ORIGIN_MEDIATOR", result.mediatorClassName.simpleName)
            setVariable("TARGET_INTERFACE_NAME", interfaceClassName.simpleName)
            setVariable("INTERNAL_CLEAN_UP_METHOD", result.internalCleanUpMethodName)
        }
        writer.write(classContent)

        return Result(
            viewModelMediatorClassName = ClassName(interfaceClassName.packageName, viewModelMediatorName)
        )
    }

    class Result(
        val viewModelMediatorClassName: ClassName,
    )
}
