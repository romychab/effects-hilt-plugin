package com.elveum.effects.processor.exceptions

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class CleanUpFunctionIsAbstractException(
    cleanUpFunctionDeclaration: KSFunctionDeclaration,
) : AbstractEffectKspException(
    message = "Clean-up function '${cleanUpFunctionDeclaration.simpleName.asString()}' should not be abstract",
    cleanUpFunctionDeclaration,
)
