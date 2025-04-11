package com.uandcode.effects.compiler.common.exceptions

import com.google.devtools.ksp.symbol.KSNode
import com.uandcode.effects.compiler.common.api.AbstractEffectKspException
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper

internal class NestedClassException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : AbstractEffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should be a top-level class",
    node,
)
