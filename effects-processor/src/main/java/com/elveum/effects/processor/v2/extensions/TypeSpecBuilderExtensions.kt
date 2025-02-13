package com.elveum.effects.processor.v2.extensions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeVariableName

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

fun implementInterfaceMethod(
    function: KSFunctionDeclaration,
    typeParameterResolver: TypeParameterResolver,
): FunSpec.Builder {
    return FunSpec.builder(function.simpleName.asString())
        .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
        .apply {
            if (function.modifiers.contains(Modifier.SUSPEND)) {
                addModifiers(KModifier.SUSPEND)
            }
        }
        .addParameters(
            function.parameters.map { param -> param.toParameterSpec(typeParameterResolver) }
        )
        .apply {
            val returnType = function.returnType?.toTypeName(typeParameterResolver)
            if (returnType != null) {
                returns(returnType)
            }
        }
        .addTypeVariables(
            function.typeParameters.map { it.toTypeVariableName(typeParameterResolver) }
        )
        .apply {
            function.typeParameters
        }
}

private fun KSValueParameter.toParameterSpec(
    typeParameterResolver: TypeParameterResolver,
): ParameterSpec {
    val paramName = name?.asString() ?: throw IllegalStateException("Can't find param name")
    return ParameterSpec.builder(paramName, type.toTypeName(typeParameterResolver))
        .apply {
            if (isVararg) {
                addModifiers(KModifier.VARARG)
            }
        }
        .build()
}