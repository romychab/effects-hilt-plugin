package com.elveum.effects.core

import com.elveum.effects.core.impl.BoundEffectControllerImpl
import kotlin.reflect.KClass

/**
 * For internal usage!
 *
 * This interface provides a mechanism for retrieving effect controllers.
 */
public interface EffectStore {

    /**
     * Get an instance of [EffectController] by using an effect [clazz].
     *
     * Each call of this method returns a new controller which manages
     * a lifecycle of a separate effect.
     *
     * @param clazz The class of an effect implementation or an interface.
     */
    public fun <T : Any> getEffectController(clazz: KClass<T>): EffectController<T>

}

/**
 * Get an instance of [EffectController] that manages an effect of type [T].
 *
 * Each call of this method returns a new controller which manages
 * a lifecycle of a separate effect.
 *
 * @see EffectController
 */
public inline fun <reified T : Any> EffectStore.getEffectController(): EffectController<T> {
    return getEffectController(T::class)
}

/**
 * Get an instance of [BoundEffectController] for the predefined effect [instance].
 *
 * @see BoundEffectController
 */
public inline fun <reified T : Any> EffectStore.getBoundEffectController(instance: T): BoundEffectController<T> {
    val effectController = getEffectController(instance::class) as EffectController<T>
    return BoundEffectControllerImpl(effectController, instance)
}
