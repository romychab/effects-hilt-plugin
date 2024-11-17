package com.elveum.effects.core

import com.elveum.effects.annotations.SideEffect
import kotlin.reflect.KClass

/**
 * This store allows getting side effect implementations (classes annotated
 * with [SideEffect]) by [KClass].
 */
public interface EffectsStore {

    /**
     * Get side effect implementation by class.
     *
     * Usage example:
     *
     * ```kotlin
     * interface Router { ... }
     *
     * @SideEffect
     * class RouterImpl : Router { ... }
     *
     * val routerImpl = effectsStore.get(RouterImpl::class)
     * ```
     */
    public fun <T : Any> get(clazz: KClass<T>): T
}

/**
 * Get side effect implementation.
 *
 * Usage example:
 *
 * ```kotlin
 * interface Router { ... }
 *
 * @SideEffect
 * class RouterImpl : Router { ... }
 *
 * val routerImpl = effectsStore.get<RouterImpl>()
 * ```
 */
public inline fun <reified T : Any> EffectsStore.get(): T {
    return get(T::class)
}
