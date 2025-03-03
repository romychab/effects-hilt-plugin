package com.elveum.effects.processor.exceptions

import com.elveum.effects.processor.extensions.KSAnnotationWrapper
import com.google.devtools.ksp.symbol.KSNode

class ClassDoesNotImplementInterfaceException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : AbstractEffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should implement at least one interface",
    node,
)
