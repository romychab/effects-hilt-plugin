package com.elveum.effects.processor.v2.exceptions

import com.elveum.effects.processor.v2.extensions.KSAnnotationWrapper
import com.google.devtools.ksp.symbol.KSNode

abstract class AbstractEffectKspException(
    message: String,
    val node: KSNode,
) : Exception(message)

/**
 * This exception can't be thrown in real scenarios
 */
class InvalidTargetArgumentException(
    effectAnnotation: KSAnnotationWrapper,
) : AbstractEffectKspException(
    message = "${effectAnnotation.printableName}(target = ...) has invalid target parameter value.",
    effectAnnotation,
)

/**
 * Internal exception in the plugin logic.
 */
class InternalEffectKspException(
    message: String,
) : Exception(message)
