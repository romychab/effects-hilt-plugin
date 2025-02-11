package com.elveum.effects.processor.v2.generators

import com.elveum.effects.processor.v2.data.EffectInfo
import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

class KspClassV2Writer(
    private val codeGenerator: CodeGenerator,
) {

    fun write(typeSpec: TypeSpec, effectInfo: EffectInfo) {
        val className = typeSpec.name
        checkNotNull(className)
        val file = FileSpec.builder(effectInfo.pkg, className)
            .addType(typeSpec)
            .build()

        file.writeTo(
            codeGenerator = codeGenerator,
            dependencies = effectInfo.dependencies,
        )
    }
}
