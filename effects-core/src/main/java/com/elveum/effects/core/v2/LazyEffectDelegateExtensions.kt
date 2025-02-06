package com.elveum.effects.core.v2

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.elveum.effects.core.v2.di.getEffectEntryPoint
import com.elveum.effects.core.v2.impl.LazyEffectDelegateImpl

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

