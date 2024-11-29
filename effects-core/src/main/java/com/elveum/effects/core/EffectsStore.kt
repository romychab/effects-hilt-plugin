package com.elveum.effects.core

import com.elveum.effects.annotations.SideEffect
import kotlin.reflect.KClass

/**
 * This store allows getting side effect implementations (classes annotated
 * with [SideEffect]) by [KClass].
 */
public interface EffectsStore {

    /**
     * Get side effect implementation (annotated with [SideEffect]) by KClass.
     *
     * Usage example:
     *
     * ```kotlin
     * interface Router { ... }
     *
     * @SideEffect
     * class RouterImpl : Router { ... }
     *
     * // get by implementation KClass
     * val routerImpl: RouterImpl = effectsStore.get(RouterImpl::class)
     * // get by interface KClass (can be casted to RouterImpl)
     * val router: Router = effectsStore.get(Router::class)
     * ```
     */
    public fun <T : Any> get(clazz: KClass<T>): T
}

/**
 * Get side effect implementation (annotated with [SideEffect]).
 *
 * Usage example:
 *
 * ```kotlin
 * interface Router { ... }
 *
 * @SideEffect
 * class RouterImpl : Router { ... }
 *
 * // get by implementation class name
 * val routerImpl: RouterImpl = effectsStore.get<RouterImpl>()
 * // get by interface class name (can be casted to RouterImpl)
 * val router: Router = effectsStore.get<Router>()
 * ```
 */
public inline fun <reified T : Any> EffectsStore.get(): T {
    return get(T::class)
}
