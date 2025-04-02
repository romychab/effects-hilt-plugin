package com.uandcode.effects.core.lifecycle

import com.uandcode.effects.core.lifecycle.internal.LazyEffectLifecycleDelegate
import kotlin.reflect.KProperty

/**
 * This interface is used as a return type of [lazyEffect] function.
 * As a result, you can use [lazyEffect] like this:
 *
 * ```
 * val effect by lazyEffect { EffectImpl() }
 * ```
 *
 * @see lazyEffect
 */
public interface EffectLifecycleDelegate<T> {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): T

    public companion object {

        /**
         * Create a lazy variant of [EffectLifecycleDelegate].
         * @param delegateCreator A lambda that creates an instance of [EffectLifecycleDelegate].
         *                        It is executed upon the first read of the property managed by
         *                        the returned lazy delegate.
         */
        public fun <T> lazy(
            delegateCreator: () -> EffectLifecycleDelegate<T>
        ): EffectLifecycleDelegate<T> {
            return LazyEffectLifecycleDelegate(delegateCreator)
        }
    }

}
