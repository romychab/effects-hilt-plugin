package com.elveum.effects.core.v2

import com.elveum.effects.core.v2.impl.BoundEffectControllerImpl
import kotlin.reflect.KClass

public interface EffectStore {
    public fun <T : Any> getEffectController(clazz: KClass<T>): EffectController<T>
}

public inline fun <reified T : Any> EffectStore.getEffectController(): EffectController<T> {
    return getEffectController(T::class)
}

public inline fun <reified T : Any> EffectStore.getBoundEffectController(instance: T): BoundEffectController<T> {
    val effectController = getEffectController(instance::class) as EffectController<T>
    return BoundEffectControllerImpl(effectController, instance)
}
