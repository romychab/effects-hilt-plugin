package com.uandcode.effects.core.compiler.exceptions

import com.google.devtools.ksp.symbol.KSNode
import com.uandcode.effects.core.compiler.api.AbstractEffectKspException
import com.uandcode.effects.core.compiler.api.extensions.KSAnnotationWrapper

internal class InterfaceWithTypeParametersException(
    effectAnnotation: KSAnnotationWrapper,
    targetInterfaceName: String,
    node: KSNode,
) : AbstractEffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should not have a target interface '$targetInterfaceName' with type parameters",
    node,
)
