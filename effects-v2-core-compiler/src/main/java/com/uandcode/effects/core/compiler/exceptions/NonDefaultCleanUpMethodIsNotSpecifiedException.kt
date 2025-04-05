package com.uandcode.effects.core.compiler.exceptions

import com.uandcode.effects.core.compiler.api.AbstractEffectKspException
import com.uandcode.effects.core.compiler.api.data.ParsedMetadata

internal class NonDefaultCleanUpMethodIsNotSpecifiedException(
    metadata: ParsedMetadata,
) : AbstractEffectKspException(
    message = "Clean-up function '${metadata.cleanUpMethodName.simpleText}' is specified in ${metadata.implementationClassDeclaration.simpleNameText}, " +
            "but not declared in the target interface: ${metadata.interfaceDeclaration.simpleNameText}",
    metadata.implementationClassDeclaration,
)