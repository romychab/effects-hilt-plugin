package com.elveum.effects.core.v2

import kotlin.reflect.KProperty

public interface LazyEffectDelegate<T> {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}
