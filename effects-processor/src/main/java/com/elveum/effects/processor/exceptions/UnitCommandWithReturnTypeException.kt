package com.elveum.effects.processor.exceptions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class UnitCommandWithReturnTypeException(
    function: KSFunctionDeclaration,
) : AbstractEffectKspException(
    "Non-suspend methods can't have a return type",
    function,
)
