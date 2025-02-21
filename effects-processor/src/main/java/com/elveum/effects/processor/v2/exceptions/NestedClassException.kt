package com.elveum.effects.processor.v2.exceptions

import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper
import com.google.devtools.ksp.symbol.KSNode

class NestedClassException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : AbstractEffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should be a top-level class",
    node,
)
