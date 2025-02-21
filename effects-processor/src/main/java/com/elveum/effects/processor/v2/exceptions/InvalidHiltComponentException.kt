package com.elveum.effects.processor.v2.exceptions

import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper

class InvalidHiltComponentException(
    effectAnnotation: KSAnnotationWrapper,
) : AbstractEffectKspException(
    "Invalid Hilt Component is specified in ${effectAnnotation.printableName}(installIn = ...). " +
            "Example of valid Hilt components: SingletonComponent::class, ActivityRetainedComponent::class, ActivityComponent::class, etc...",
    effectAnnotation,
)
