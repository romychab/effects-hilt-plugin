package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import kotlin.reflect.KClass

internal object EmptyEffectComponent : AbstractEffectComponent() {

    override fun <T : Any> get(clazz: KClass<T>): T {
        throw EffectNotFoundException(clazz)
    }

    override fun <T : Any> getController(clazz: KClass<T>): EffectController<T> {
        throw EffectNotFoundException(clazz)
    }

    override fun <T : Any> getBoundController(
        clazz: KClass<T>,
        provider: () -> T
    ): BoundEffectController<T> {
        throw EffectNotFoundException(clazz)
    }

    override fun cleanUp() = Unit

}
