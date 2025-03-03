package com.elveum.effects.processor.exceptions

import com.elveum.effects.processor.data.EffectMetadata

class NonDefaultCleanUpMethodIsNotSpecifiedException(
    metadata: EffectMetadata,
) : AbstractEffectKspException(
    message = "Clean-up function '${metadata.cleanUpMethodName.simpleText}' is specified in ${metadata.effectClassDeclaration.simpleNameText}, " +
            "but not declared in the target interface: ${metadata.targetInterfaceDeclaration.simpleNameText}",
    metadata.effectClassDeclaration,
)