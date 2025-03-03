package com.elveum.effects.processor.exceptions

import com.elveum.effects.processor.extensions.KSAnnotationWrapper
import com.google.devtools.ksp.symbol.KSNode

class InterfaceWithTypeParametersException(
    effectAnnotation: KSAnnotationWrapper,
    targetInterfaceName: String,
    node: KSNode,
) : AbstractEffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should not have a target interface '$targetInterfaceName' with type parameters",
    node,
)
