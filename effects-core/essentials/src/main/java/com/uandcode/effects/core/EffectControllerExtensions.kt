package com.uandcode.effects.core

import com.uandcode.effects.core.exceptions.ControllerAlreadyStartedException
import com.uandcode.effects.core.internal.BoundEffectControllerImpl

/**
 * Create a [BoundEffectController] based on existing [EffectController].
 *
 * BoundEffectController is a controller that is bound to a specific instance of the effect
 * provided by a [provider] function. This function is executed lazily and only once when
 * you call [BoundEffectController.start] method or when you try to get an effect instance
 * through [BoundEffectController.effectImplementation] property.
 *
 * @throws ControllerAlreadyStartedException
 */
public fun <T : Any> EffectController<T>.bind(
    provider: () -> T
): BoundEffectController<T> {
    if (this.isStarted) {
        throw ControllerAlreadyStartedException()
    }
    return BoundEffectControllerImpl(this, provider)
}
