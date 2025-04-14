package com.uandcode.effects.koin.compiler

import com.uandcode.effects.compiler.common.api.AbstractMetadataGenerator
import com.uandcode.effects.compiler.common.api.KspClassWriter
import com.uandcode.effects.compiler.common.api.data.ParsedEffect
import com.uandcode.effects.compiler.common.api.data.TemplateBasedClassContent
import com.uandcode.effects.koin.compiler.data.KoinParsedEffect

class KoinMetadataGenerator(
    writer: KspClassWriter,
) : AbstractMetadataGenerator(
    templatePath = "com/uandcode/effects/koin/compiler/KoinMetadataTemplate.kt",
    writer = writer,
) {
    override fun TemplateBasedClassContent.setupVariables(parsedEffect: ParsedEffect) {
        val koinScopeString = (parsedEffect as KoinParsedEffect).koinScope.toMetadataString()
        setVariable("EFFECT_KOIN_SCOPE", koinScopeString)
    }
}
