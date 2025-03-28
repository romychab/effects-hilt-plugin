package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.EffectController
import kotlin.reflect.KClass

internal object EmptyEffectComponent : AbstractEffectComponent() {

    override fun <T : Any> get(clazz: KClass<T>): T {
        throwException(clazz)
    }

    override fun <T : Any> getController(clazz: KClass<T>): EffectController<T> {
        throwException(clazz)
    }

    override fun <T : Any> getBoundController(
        clazz: KClass<T>,
        provider: () -> T
    ): BoundEffectController<T> {
        throwException(clazz)
    }

    override fun cleanUp() = Unit

    private fun throwException(clazz: KClass<*>): Nothing {
        throw IllegalArgumentException(
            "Can't find an effect for '$clazz'. Make sure 1) you use annotations correctly 2) the effect is accessed from the correct component."
        )
    }
}
