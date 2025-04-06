package com.uandcode.effects.core.compiler.api.extensions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName

/**
 * Implement the interface specified by [interfaceDeclaration].
 * @param functionBody a lambda that is called for each abstract function in the interface.
 */
public fun TypeSpec.Builder.implementInterface(
    interfaceDeclaration: KSClassDeclarationWrapper,
    filter: (KSFunctionDeclaration) -> Boolean = { true },
    functionBody: (KSFunctionDeclarationWrapper) -> CodeBlock,
): TypeSpec.Builder {
    return addSuperinterface(interfaceDeclaration.toClassName())
        .apply {
            interfaceDeclaration.getAllFunctions()
                .filter { it.isAbstract && filter(it) }
                .forEach { function ->
                    val typeParameterResolver = function.typeParameters.toTypeParameterResolver()
                    val funSpecBuilder = implementInterfaceMethod(function, typeParameterResolver)
                    val functionDeclarationWrapper = KSFunctionDeclarationWrapper(funSpecBuilder, function, typeParameterResolver)
                    val codeBlock = functionBody(functionDeclarationWrapper)
                    funSpecBuilder.addCode(codeBlock)
                    addFunction(funSpecBuilder.build())
                }
        }
}

public fun implementInterfaceMethod(
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
