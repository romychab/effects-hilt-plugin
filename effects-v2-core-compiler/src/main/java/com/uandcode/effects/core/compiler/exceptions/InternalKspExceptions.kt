package com.uandcode.effects.core.compiler.exceptions

import com.uandcode.effects.core.compiler.api.AbstractEffectKspException
import com.uandcode.effects.core.compiler.api.extensions.KSAnnotationWrapper

/**
 * This exception can't be thrown in real scenarios
 */
internal class InvalidTargetArgumentException(
    effectAnnotation: KSAnnotationWrapper,
) : AbstractEffectKspException(
    message = "${effectAnnotation.printableName}(target = ...) has invalid target parameter value.",
    effectAnnotation,
)

/**
 * Internal exception in the plugin logic.
 */
internal class InternalEffectKspException(
    message: String,
) : Exception(message)
