package com.uandcode.effects.core.compiler.exceptions

import com.uandcode.effects.core.compiler.api.AbstractEffectKspException
import com.uandcode.effects.core.compiler.api.extensions.KSAnnotationWrapper

internal class TargetInterfaceIsNotSpecifiedException(
    effectAnnotation: KSAnnotationWrapper,
) : AbstractEffectKspException(
    message = "${effectAnnotation.printableName}(target = ...) parameter should be specified if your class " +
            "implements more than 1 interface",
    effectAnnotation,
)
