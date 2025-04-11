package com.uandcode.effects.compiler.common.exceptions

import com.uandcode.effects.compiler.common.api.AbstractEffectKspException
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper

internal class TargetInterfaceIsNotSpecifiedException(
    effectAnnotation: KSAnnotationWrapper,
) : AbstractEffectKspException(
    message = "${effectAnnotation.printableName}(target = ...) parameter should be specified if your class " +
            "implements more than 1 interface",
    effectAnnotation,
)
