package com.elveum.effects.core.v2

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.elveum.effects.annotations.HiltEffect
import com.elveum.effects.core.v2.di.getEffectEntryPoint
import com.elveum.effects.core.v2.impl.LazyEffectDelegateImpl

/**
 * Get an instance of an effect class annotated with [HiltEffect].
 *
 * The instance created by [initializer] automatically connects to
 * the target interface when the activity goes to a STARTED state, and then
 * automatically disconnects from the target when the activity is stopped.
 *
 * Usage example:
 *
 * ```
 * val myEffectImpl by lazyEffect {
 *   MyEffectImpl()
 * }
 * ```
 */
public inline fun <reified T : Any> ComponentActivity.lazyEffect(
    noinline initializer: () -> T,
): LazyEffectDelegate<T> {
    return LazyEffectDelegateImpl(
        lifecycle = this.lifecycle,
        effectControllerProvider = {
            getEffectEntryPoint().getEffectStore().getBoundEffectController(initializer())
        },
    )
}

/**
 * Get an instance of an effect class annotated with [HiltEffect].
 *
 * The instance created by [initializer] automatically connects to
 * the target interface when a fragment goes to a STARTED state, and then
 * automatically disconnects from the target when the fragment is stopped.
 *
 * Usage example:
 *
 * ```
 * val myEffectImpl by lazyEffect {
 *   MyEffectImpl()
 * }
 * ```
 */
public inline fun <reified T : Any> Fragment.lazyEffect(
    noinline initializer: () -> T,
): LazyEffectDelegate<T> {
    return LazyEffectDelegateImpl(
        lifecycle = this.lifecycle,
        effectControllerProvider = {
            getEffectEntryPoint().getEffectStore().getBoundEffectController(initializer())
        },
    )
}
