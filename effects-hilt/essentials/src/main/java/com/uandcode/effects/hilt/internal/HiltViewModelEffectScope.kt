package com.uandcode.effects.hilt.internal

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.hilt.InternalEffectsHiltApi
import dagger.hilt.android.ViewModelLifecycle
import kotlin.reflect.KClass

@InternalEffectsHiltApi
public class HiltViewModelEffectScope(
    private val origin: EffectScope,
    private val viewModelLifecycle: ViewModelLifecycle,
) : EffectScope by origin {

    override fun <T : Any> getProxy(clazz: KClass<T>): T {
        val proxyEffect = origin.getProxy(clazz)
        if (proxyEffect is AutoCloseable) {
            viewModelLifecycle.addOnClearedListener {
                proxyEffect.close()
            }
        }
        return proxyEffect
    }

}
