package com.uandcode.effects.koin.compiler.exception

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.uandcode.effects.compiler.common.api.AbstractEffectKspException
import com.uandcode.effects.koin.compiler.data.KoinScope

class InconsistentScopesException(
    interfaceDeclaration: KSClassDeclaration,
    effectImpl1: ClassName,
    scope1: KoinScope,
    effectImpl2: ClassName,
    scope2: KoinScope,
) : AbstractEffectKspException(
    "Interface '${interfaceDeclaration.simpleName.asString()}' has implementations with different " +
    "declared scopes: 1) ${effectImpl1.simpleName} declares scope: $scope1 2) ${effectImpl2.simpleName} declares scope: $scope2.",
    interfaceDeclaration,
)
