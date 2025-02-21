package com.elveum.effects.processor.v2.exceptions

import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper

class InvalidInstallInArgumentException(
    effectAnnotation: KSAnnotationWrapper,
) : AbstractEffectKspException(
    message = "Invalid installIn parameter value in ${effectAnnotation.printableName}(installIn = ...) annotation. " +
            "Valid values are: @SingletonComponent, @ActivityRetainedComponent, @ActivityComponent and @FragmentComponent.",
    effectAnnotation,
)
