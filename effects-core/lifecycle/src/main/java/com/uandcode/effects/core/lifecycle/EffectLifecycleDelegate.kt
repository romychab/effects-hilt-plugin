package com.uandcode.effects.core.lifecycle

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
}
