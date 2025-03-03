package com.elveum.effects.processor.extensions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.TypeParameterResolver

class KSFunctionDeclarationWrapper(
    private val funSpecBuilder: FunSpec.Builder,
    private val functionDeclaration: KSFunctionDeclaration,
    val typeParameterResolver: TypeParameterResolver,
) : KSFunctionDeclaration by functionDeclaration {

    val paramSpecs: List<ParameterSpec> get() = funSpecBuilder.parameters

    val simpleNameString: String get() = functionDeclaration.simpleName.asString()

}