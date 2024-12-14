package com.elveum.effects.core

import com.elveum.effects.annotations.MviEffect
import kotlin.reflect.KClass

/**
 * This store allows getting MVI-effect implementations (classes annotated
 * with [MviEffect]) by [KClass].
 */
public interface MviEffectsStore {

    /**
     * Get MVI-effect implementation (annotated with [MviEffect]) by KClass.
     *
     * Usage example:
     *
     * ```kotlin
     * interface Router { ... }
     *
     * @MviEffect
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
 * Get MVI-effect implementation (annotated with [MviEffect]).
 *
 * Usage example:
 *
 * ```kotlin
 * interface Router { ... }
 *
 * @MviEffect
 * class RouterImpl : Router { ... }
 *
 * // get by implementation class name
 * val routerImpl: RouterImpl = effectsStore.get<RouterImpl>()
 * // get by interface class name (can be casted to RouterImpl)
 * val router: Router = effectsStore.get<Router>()
 * ```
 */
public inline fun <reified T : Any> MviEffectsStore.get(): T {
    return get(T::class)
}
