package com.uandcode.effects.core.compiler.exceptions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.uandcode.effects.core.compiler.api.AbstractEffectKspException

internal class UnitCommandWithReturnTypeException(
    function: KSFunctionDeclaration,
) : AbstractEffectKspException(
    "Non-suspend methods can't have a return type",
    function,
)
