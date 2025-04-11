package com.uandcode.effects.compiler.common.generators

import com.uandcode.effects.compiler.common.api.AbstractMetadataGenerator
import com.uandcode.effects.compiler.common.api.KspClassWriter

internal class DefaultMetadataGenerator(
    writer: KspClassWriter,
) : AbstractMetadataGenerator(
    templatePath = "com/uandcode/effects/core/compiler/MetadataTemplate.kt",
    writer = writer,
)
