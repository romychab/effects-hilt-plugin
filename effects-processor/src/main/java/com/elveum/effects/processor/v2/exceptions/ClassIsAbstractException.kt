package com.elveum.effects.processor.v2.exceptions

import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper
import com.google.devtools.ksp.symbol.KSNode

class ClassIsAbstractException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : AbstractEffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should not be abstract",
    node,
)
