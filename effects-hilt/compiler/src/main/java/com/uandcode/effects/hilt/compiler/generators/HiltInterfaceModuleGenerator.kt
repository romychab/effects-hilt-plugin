package com.uandcode.effects.hilt.compiler.generators

import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.TemplateBasedClassContent
import com.uandcode.effects.compiler.common.api.extensions.KSClassDeclarationWrapper
import com.uandcode.effects.hilt.compiler.data.HiltParsedMetadata

class HiltInterfaceModuleGenerator(
    private val writer: KspClassWriter,
) {

    fun generate(
        interfaceDeclaration: KSClassDeclarationWrapper,
        metadata: HiltParsedMetadata,
    ) {
        val moduleName = "${interfaceDeclaration.simpleNameText}Module"
        val pkg = interfaceDeclaration.packageName.asString()
        val content = TemplateBasedClassContent(
            className = moduleName,
            pkg = pkg,
            templatePath = "com/uandcode/effects/hilt/compiler/HiltInterfaceModuleTemplate.kt",
            dependencies = metadata.dependencies,
        ).apply {
            setVariable("HILT_COMPONENT", metadata.hiltComponent.canonicalName)
            setVariable("HILT_COMPONENT_NAME", metadata.hiltComponent.simpleName)
            setVariable("TARGET_INTERFACE_NAME", interfaceDeclaration.simpleNameText)
            setVariable("QUALIFIER", metadata.hiltComponent.qualifier)
        }
        writer.write(content)
    }
}
