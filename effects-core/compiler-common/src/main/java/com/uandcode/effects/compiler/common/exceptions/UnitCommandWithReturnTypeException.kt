package com.uandcode.effects.compiler.common.exceptions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.uandcode.effects.compiler.common.api.AbstractEffectKspException

internal class UnitCommandWithReturnTypeException(
    function: KSFunctionDeclaration,
) : AbstractEffectKspException(
    "Non-suspend methods can't have a return type",
    function,
)
