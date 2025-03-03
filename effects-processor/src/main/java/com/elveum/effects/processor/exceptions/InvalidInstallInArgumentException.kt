package com.elveum.effects.processor.exceptions

import com.elveum.effects.processor.extensions.KSAnnotationWrapper

class InvalidInstallInArgumentException(
    effectAnnotation: KSAnnotationWrapper,
) : AbstractEffectKspException(
    message = "Invalid installIn parameter value in ${effectAnnotation.printableName}(installIn = ...) annotation. " +
            "Valid values are: @SingletonComponent, @ActivityRetainedComponent, @ActivityComponent and @FragmentComponent.",
    effectAnnotation,
)
