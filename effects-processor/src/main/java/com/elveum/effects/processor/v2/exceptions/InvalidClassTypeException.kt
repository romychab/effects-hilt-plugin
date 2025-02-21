package com.elveum.effects.processor.v2.exceptions

import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper
import com.google.devtools.ksp.symbol.KSNode

class InvalidClassTypeException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : AbstractEffectKspException(
    message = "Symbol annotated with @${effectAnnotation.simpleName} should be a Class or Object",
    node,
)
