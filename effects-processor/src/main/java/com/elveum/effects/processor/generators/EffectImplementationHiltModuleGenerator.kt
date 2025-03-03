package com.elveum.effects.processor.generators

import com.elveum.effects.processor.data.EffectInfo
import com.elveum.effects.processor.generators.base.KspClassWriter
import com.elveum.effects.processor.generators.base.TemplateBasedClassContent

class EffectImplementationHiltModuleGenerator(
    private val writer: KspClassWriter,
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
            setVariable("TARGET_INTERFACE_CLASSNAME", effectInfo.targetInterfaceClassName.canonicalName)
            setVariable("HILT_COMPONENT", effectInfo.hiltComponent.canonicalName)
            setVariable("HILT_COMPONENT_NAME", effectInfo.hiltComponent.simpleName)
        }
        writer.write(classContent)
    }

}
