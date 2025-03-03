package com.elveum.effects.processor.exceptions

import com.elveum.effects.processor.extensions.KSAnnotationWrapper

class TargetInterfaceIsNotSpecifiedException(
    effectAnnotation: KSAnnotationWrapper,
) : AbstractEffectKspException(
    message = "${effectAnnotation.printableName}(target = ...) parameter should be specified if your class " +
            "implements more than 1 interface",
    effectAnnotation,
)
