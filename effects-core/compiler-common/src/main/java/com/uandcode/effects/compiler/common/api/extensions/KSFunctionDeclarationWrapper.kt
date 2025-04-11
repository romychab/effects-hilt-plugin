package com.uandcode.effects.compiler.common.api.extensions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.TypeParameterResolver

/**
 * A wrapper for [KSFunctionDeclaration] that provides additional functionality.
 * For now, it provides only parameters of the function and its name, but additional
 * functionality can be added in the future.
 */
public data class KSFunctionDeclarationWrapper(
    private val funSpecBuilder: FunSpec.Builder,
    private val functionDeclaration: KSFunctionDeclaration,
    val typeParameterResolver: TypeParameterResolver,
) : KSFunctionDeclaration by functionDeclaration {

    val paramSpecs: List<ParameterSpec> get() = funSpecBuilder.parameters

    val simpleNameString: String get() = functionDeclaration.simpleName.asString()

}
