package com.uandcode.effects.hilt

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.bind

public inline fun <reified T : Any> ComponentActivity.lazyEffect(
    noinline provider: () -> T,
): HiltEffectDelegate<T> {
    return lazyEffect(
        scopeProvider = { getEffectEntryPoint().getEffectScope() },
        effectProvider = provider,
    )
}

public inline fun <reified T : Any> Fragment.lazyEffect(
    noinline provider: () -> T,
): HiltEffectDelegate<T> {
    return lazyEffect(
        scopeProvider = { getEffectEntryPoint().getEffectScope() },
        effectProvider = provider,
    )
}

@PublishedApi
internal inline fun <reified T : Any> LifecycleOwner.lazyEffect(
    crossinline scopeProvider: () -> EffectScope,
    noinline effectProvider: () -> T,
): HiltEffectDelegate<T> {
    return HiltEffectDelegateImpl(
        lifecycle = this.lifecycle,
        controllerProvider = {
            scopeProvider().getController(T::class).bind(effectProvider)
        }
    )
}
