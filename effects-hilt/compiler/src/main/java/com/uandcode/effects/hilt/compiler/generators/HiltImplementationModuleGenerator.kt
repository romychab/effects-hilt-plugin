package com.uandcode.effects.hilt.compiler.generators

import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.TemplateBasedClassContent
import com.uandcode.effects.hilt.compiler.data.HiltParsedMetadata

class HiltImplementationModuleGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(metadata: HiltParsedMetadata) {
        val implementationClassName = metadata.implementationClassName
        val moduleName = "${implementationClassName.simpleName}Module"
        val pkg = implementationClassName.packageName
        val content = TemplateBasedClassContent(
            className = moduleName,
            pkg = pkg,
            templatePath = "com/uandcode/effects/hilt/compiler/HiltImplementationModuleTemplate.kt",
            dependencies = metadata.dependencies,
        ).apply {
            setVariable("HILT_COMPONENT", metadata.hiltComponent.canonicalName)
            setVariable("HILT_COMPONENT_NAME", metadata.hiltComponent.simpleName)
            setVariable("EFFECT_NAME", metadata.implementationClassName.simpleName)
        }
        writer.write(content)
    }

}
