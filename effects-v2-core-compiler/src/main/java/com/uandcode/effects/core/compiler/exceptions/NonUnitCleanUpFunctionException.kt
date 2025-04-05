package com.uandcode.effects.core.compiler.exceptions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.uandcode.effects.core.compiler.api.AbstractEffectKspException

internal class NonUnitCleanUpFunctionException(
    cleanUpFunctionDeclaration: KSFunctionDeclaration,
) : AbstractEffectKspException(
    message = "Clean-up function '${cleanUpFunctionDeclaration.simpleName.asString()}' should not return any type",
    cleanUpFunctionDeclaration,
)
