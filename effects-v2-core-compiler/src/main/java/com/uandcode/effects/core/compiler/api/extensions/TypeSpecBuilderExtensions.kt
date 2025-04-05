package com.uandcode.effects.core.compiler.api.extensions

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

/**
 * Define a primary constructor to the [TypeSpec.Builder]
 * which automatically adds 'val' keyword to each parameter.
 *
 * @param funSpec Must be created by [FunSpec.constructorBuilder] call.
 */
public fun TypeSpec.Builder.primaryConstructorWithProperties(
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
