package com.elveum.effects.processor.v2.generators

import com.elveum.effects.processor.v2.data.EffectInfo
import com.elveum.effects.processor.v2.generators.base.KspClassV2Writer
import com.elveum.effects.processor.v2.generators.base.TemplateBasedClassContent

class EffectHiltModuleGenerator(
    private val writer: KspClassV2Writer,
) {

    fun generate(
        effectInfo: EffectInfo,
        result: EffectMediatorGenerator.Result,
    ) {
        val className = "${effectInfo.targetInterfaceName}Module"
        val packageStatement = if (effectInfo.pkg.isBlank()) {
            ""
        } else {
            "package ${effectInfo.pkg}"
        }
        val classContent = TemplateBasedClassContent.Builder("EffectHiltModuleTemplate")
            .setPackage(effectInfo.pkg)
            .setClassName(className)
            .setDependencies(effectInfo.dependencies)
            .setVariable("PACKAGE_STATEMENT", packageStatement)
            .setVariable("TARGET_INTERFACE_NAME", effectInfo.targetInterfaceName)
            .setVariable("EFFECT_IMPL_NAME", effectInfo.effectClassName.simpleName)
            .setVariable("HILT_COMPONENT", effectInfo.hiltComponent.canonicalName)
            .setVariable("HILT_SCOPE", effectInfo.hiltScope.canonicalName)
            .setVariable("MEDIATOR_NAME", result.mediatorClassName.simpleName)
            .build()
        writer.write(classContent)
    }

}