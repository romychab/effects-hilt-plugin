package com.uandcode.effects.hilt.compiler.generators

import com.uandcode.effects.compiler.common.api.AbstractMetadataGenerator
import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.api.data.TemplateBasedClassContent
import com.uandcode.effects.hilt.compiler.data.HiltParsedEffect

class HiltMetadataGenerator(
    writer: KspClassWriter,
) : AbstractMetadataGenerator(
    templatePath = "com/uandcode/effects/hilt/compiler/HiltMetadataTemplate.kt",
    writer = writer,
) {

    override fun TemplateBasedClassContent.setupVariables(parsedEffect: ParsedEffect) {
        val hiltParsedEffect = parsedEffect as HiltParsedEffect
        setVariable("HILT_COMPONENT_CLASSNAME", hiltParsedEffect.hiltComponent.canonicalName)
    }

}
