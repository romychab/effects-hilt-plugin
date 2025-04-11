package com.uandcode.effects.compiler.common.exceptions

import com.google.devtools.ksp.symbol.KSNode
import com.uandcode.effects.compiler.common.api.AbstractEffectKspException
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper

internal class InvalidClassTypeException(
    effectAnnotation: KSAnnotationWrapper,
    node: KSNode,
) : AbstractEffectKspException(
    message = "Symbol annotated with @${effectAnnotation.simpleName} should be a Class or Object",
    node,
)
