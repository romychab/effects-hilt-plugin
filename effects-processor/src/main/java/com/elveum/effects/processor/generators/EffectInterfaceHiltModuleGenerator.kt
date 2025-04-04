package com.elveum.effects.processor.generators

import com.elveum.effects.processor.data.EffectMetadata
import com.elveum.effects.processor.generators.base.KspClassWriter
import com.elveum.effects.processor.generators.base.TemplateBasedClassContent

class EffectInterfaceHiltModuleGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(
        effectMetadata: EffectMetadata,
        result: EffectMediatorGenerator.Result,
    ) {
        val classContent = TemplateBasedClassContent(
            templatePath = "EffectInterfaceHiltModuleTemplate.kt",
            pkg = effectMetadata.pkg,
            className = "${effectMetadata.targetInterfaceName}EffectModule",
            dependencies = effectMetadata.dependencies
        ).apply {
            setVariable("TARGET_INTERFACE_NAME", effectMetadata.targetInterfaceName)
            setVariable("HILT_COMPONENT", effectMetadata.hiltComponent.canonicalName)
            setVariable("HILT_COMPONENT_NAME", effectMetadata.hiltComponent.simpleName)
            setVariable("HILT_SCOPE", effectMetadata.hiltScope.canonicalName)
            setVariable("HILT_SCOPE_NAME", effectMetadata.hiltScope.simpleName)
            setVariable("MEDIATOR_NAME", result.mediatorClassName.simpleName)
            setVariable("PRIORITY_CONST", effectMetadata.getHiltComponentPriorityConstName())
        }
        writer.write(classContent)
    }

}
