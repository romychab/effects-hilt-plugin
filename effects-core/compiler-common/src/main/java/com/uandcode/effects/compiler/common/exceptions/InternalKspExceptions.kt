package com.uandcode.effects.compiler.common.exceptions

import com.uandcode.effects.compiler.common.api.AbstractEffectKspException
import com.uandcode.effects.compiler.common.api.extensions.KSAnnotationWrapper

/**
 * This exception can't be thrown in real scenarios
 */
internal class InvalidAnnotationArgumentException(
    effectAnnotation: KSAnnotationWrapper,
) : AbstractEffectKspException(
    message = "${effectAnnotation.printableName} has invalid parameter value.",
    effectAnnotation,
)

/**
 * Internal exception in the plugin logic.
 */
internal class InternalEffectKspException(
    message: String,
) : Exception(message)
