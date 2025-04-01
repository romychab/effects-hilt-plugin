package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.EffectController
import kotlin.reflect.KClass

internal class LazyEffectComponent(
    private val provider: () -> EffectComponent,
) : EffectComponent {

    private val origin by lazy { provider() }

    override fun <T : Any> get(clazz: KClass<T>): T {
        return origin.get(clazz)
    }

    override fun <T : Any> getController(clazz: KClass<T>): EffectController<T> {
        return origin.getController(clazz)
    }

    override fun <T : Any> getBoundController(
        clazz: KClass<T>,
        provider: () -> T
    ): BoundEffectController<T> {
        return origin.getBoundController(clazz, provider)
    }

    override fun createChild(vararg interfaces: KClass<*>): EffectComponent {
        return origin.createChild(*interfaces)
    }

    override fun cleanUp() {
        origin.cleanUp()
    }

}
