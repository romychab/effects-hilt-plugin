package com.uandcode.effects.core.compiler

import com.uandcode.effects.compiler.common.api.AbstractMetadataGenerator
import com.uandcode.effects.compiler.common.api.KspClassWriter

internal class CoreMetadataGenerator(
    writer: KspClassWriter,
) : AbstractMetadataGenerator(
    templatePath = "com/uandcode/effects/core/compiler/MetadataTemplate.kt",
    writer = writer,
)
