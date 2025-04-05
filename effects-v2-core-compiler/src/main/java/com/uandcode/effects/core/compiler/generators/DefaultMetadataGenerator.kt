package com.uandcode.effects.core.compiler.generators

import com.uandcode.effects.core.compiler.api.AbstractMetadataGenerator
import com.uandcode.effects.core.compiler.api.KspClassWriter

internal class DefaultMetadataGenerator(
    writer: KspClassWriter,
) : AbstractMetadataGenerator(
    templatePath = "com/uandcode/effects/core/compiler/MetadataTemplate.kt",
    writer = writer,
)
