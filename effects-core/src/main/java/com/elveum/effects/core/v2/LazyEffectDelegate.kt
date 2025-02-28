package com.elveum.effects.core.v2

import kotlin.reflect.KProperty

/**
 * A Kotlin Property Delegate created by [lazyEffect] function.
 *
 * @see lazyEffect
 */
public interface LazyEffectDelegate<T> {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}
