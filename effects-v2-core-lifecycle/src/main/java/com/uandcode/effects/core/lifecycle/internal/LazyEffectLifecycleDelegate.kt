package com.uandcode.effects.core.lifecycle.internal

import com.uandcode.effects.core.lifecycle.EffectLifecycleDelegate
import kotlin.reflect.KProperty

internal class LazyEffectLifecycleDelegate<T>(
    delegateCreator: () -> EffectLifecycleDelegate<T>,
) : EffectLifecycleDelegate<T> {

    private val origin by lazy(delegateCreator)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return origin.getValue(thisRef, property)
    }

}
