package com.elveum.effects.processor.exceptions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class NonUnitCleanUpFunctionException(
    cleanUpFunctionDeclaration: KSFunctionDeclaration,
) : AbstractEffectKspException(
    message = "Clean-up function '${cleanUpFunctionDeclaration.simpleName.asString()}' should not return any type",
    cleanUpFunctionDeclaration,
)
