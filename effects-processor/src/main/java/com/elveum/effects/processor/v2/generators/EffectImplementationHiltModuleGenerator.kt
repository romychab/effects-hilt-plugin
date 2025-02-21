package com.elveum.effects.processor.v2.generators

import com.elveum.effects.processor.v2.data.EffectInfo
import com.elveum.effects.processor.v2.generators.base.KspClassV2Writer
import com.elveum.effects.processor.v2.generators.base.TemplateBasedClassContent

class EffectImplementationHiltModuleGenerator(
    private val writer: KspClassV2Writer,
) {

    fun generate(effectInfo: EffectInfo) {
        val classContent = TemplateBasedClassContent(
            templatePath = "EffectImplementationHiltModuleTemplate.kt",
            pkg = effectInfo.pkg,
            dependencies = effectInfo.dependencies,
            className = "${effectInfo.effectName}ImplModule",
        ).apply {
            setVariable("EFFECT_IMPL_NAME", effectInfo.effectName)
            setVariable("TARGET_INTERFACE_NAME", effectInfo.targetInterfaceName)
            setVariable("HILT_COMPONENT", effectInfo.hiltComponent.canonicalName)
            setVariable("HILT_COMPONENT_NAME", effectInfo.hiltComponent.simpleName)
        }
        writer.write(classContent)
    }

}
