package com.uandcode.effects.compiler.common.exceptions

import com.google.devtools.ksp.symbol.KSNode
import com.uandcode.effects.compiler.common.api.AbstractEffectKspException
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper

internal class InterfaceWithTypeParametersException(
    effectAnnotation: KSAnnotationWrapper,
    targetInterfaceName: String,
    node: KSNode,
) : AbstractEffectKspException(
    message = "Class annotated with @${effectAnnotation.simpleName} should not have a target interface '$targetInterfaceName' with type parameters",
    node,
)
