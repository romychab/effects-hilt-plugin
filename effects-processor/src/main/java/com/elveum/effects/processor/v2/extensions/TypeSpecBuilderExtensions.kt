package com.elveum.effects.processor.v2.extensions

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

fun TypeSpec.Builder.primaryConstructorWithProperties(
    funSpec: FunSpec,
): TypeSpec.Builder {
    return this
        .primaryConstructor(funSpec)
        .apply {
            funSpec.parameters.forEach { param ->
                addProperty(
                    PropertySpec.builder(param.name, param.type, KModifier.PRIVATE)
                        .initializer(param.name)
                        .build()
                )
            }
        }
}
