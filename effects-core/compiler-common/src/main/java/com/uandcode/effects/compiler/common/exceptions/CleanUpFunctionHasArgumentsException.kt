package com.uandcode.effects.compiler.common.exceptions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.uandcode.effects.compiler.common.api.AbstractEffectKspException

internal class CleanUpFunctionHasArgumentsException(
    cleanUpFunctionDeclaration: KSFunctionDeclaration,
) : AbstractEffectKspException(
    message = "Clean-up function '${cleanUpFunctionDeclaration.simpleName.asString()}' should not have parameters",
    cleanUpFunctionDeclaration,
)
