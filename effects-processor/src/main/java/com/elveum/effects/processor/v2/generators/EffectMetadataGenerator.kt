package com.elveum.effects.processor.v2.generators

import com.elveum.effects.processor.v2.data.Const
import com.elveum.effects.processor.v2.data.EffectInfo
import com.elveum.effects.processor.v2.generators.base.KspClassV2Writer
import com.elveum.effects.processor.v2.generators.base.TemplateBasedClassContent

class EffectMetadataGenerator(
    private val writer: KspClassV2Writer,
) {

    fun generate(effectInfo: EffectInfo) {
        val prefixName = effectInfo.effectClassName.canonicalName.replace('.', '_')
        val classContent = TemplateBasedClassContent(
            templatePath = "EffectMetadataTemplate.kt",
            pkg = Const.MetadataPackage,
            className = "__${prefixName}_Metadata",
            dependencies = effectInfo.dependencies,
        ).apply {
            setVariable("EFFECT_IMPL_CLASSNAME", effectInfo.effectClassName.canonicalName)
            setVariable("TARGET_INTERFACE_CLASSNAME", effectInfo.targetInterfaceClassName.canonicalName)
            setVariable("HILT_COMPONENT_CLASSNAME", effectInfo.hiltComponent.canonicalName)
            setVariable("HILT_SCOPE_CLASSNAME", effectInfo.hiltScope.canonicalName)
            setVariable("CLEAN_UP_METHOD_NAME", effectInfo.cleanUpMethodName.originCleanUpMethodName)
        }
        writer.write(classContent)
    }

}
