package com.uandcode.effects.core

import kotlin.reflect.KClass

/**
 * Represents a scoped collection of auto-generated proxy effects.
 *
 * You can use a pre-defined global effect component where all proxy effects
 * are singletons:
 *
 * ```
 * val component = RootEffectComponents.global
 * ```
 *
 * Alternatively, you can create your own components with different lifecycles
 * for each effect interface:
 *
 * ```
 * // store this variable somewhere in a singleton object
 * val singletonComponent = RootEffectComponents.empty.createChild(
 *     // the list of target effect interfaces that should be singletons
 *     SingletonEffectInterface1::class,
 *     SingletonEffectInterface2::class,
 * )
 *
 * // create this variable somewhere in Activity ViewModel:
 * val activityRetainedComponent = singletonComponent.createChild(
 *     // the list of target effect interfaces that should be scoped to
 *     // activity view-model lifecycle
 *     MyActivityRetainedEffectInterface1::class,
 *     MyActivityRetainedEffectInterface2::class,
 * )
 * ```
 */
public interface EffectComponent {

    /**
     * Get an auto-generated proxy class of target interface [T].
     *
     * @param clazz KClass representing an effect interface (only interfaces are allowed)
     * @throws IllegalArgumentException if the specified [T] interface is not a valid target
     * @throws IllegalStateException if the library is not setup correctly
     */
    public fun <T : Any> get(clazz: KClass<T>): T

    /**
     * Get a new controller for attaching an effect implementation to a target effect interface
     *
     * @param clazz KClass representing either an effect interface or its annotated child class
     * @throws IllegalArgumentException if the specified [T] type is not a valid target interface or
     *                                  it is not a child class of valid interface
     * @throws IllegalStateException if the library is not setup correctly
     */
    public fun <T : Any> getController(clazz: KClass<T>): EffectController<T>

    /**
     * Get a new controller for attaching an effect implementation to a target effect interface.
     * This is a bound controller with pre-defined effect implementation provided by [provider] argument.
     *
     * @param clazz KClass representing either an effect interface or its annotated child class
     * @throws IllegalArgumentException if the specified [T] type is not a valid target interface or
     *                                  it is not a child class of valid interface
     * @throws IllegalStateException if the library is not setup correctly
     */
    public fun <T : Any> getBoundController(
        clazz: KClass<T>,
        provider: () -> T,
    ): BoundEffectController<T>

    /**
     * Create a child [EffectComponent] instance that inherits all effects
     * from this parent component and additionally it can provide proxy effects
     * listed in a [interfaces] argument.
     */
    public fun createChild(vararg interfaces: KClass<*>): EffectComponent

    /**
     * Cancel all pending non-finished calls executed on a generated
     * proxy instance returned by [get] method.
     */
    public fun cleanUp()

}

/**
 * @see EffectComponent.get
 */
public inline fun <reified T : Any> EffectComponent.get(): T {
    return get(T::class)
}

/**
 * @see EffectComponent.getController
 */
public inline fun <reified T : Any> EffectComponent.getController(): EffectController<T> {
    return getController(T::class)
}

/**
 * @see EffectComponent.getBoundController
 */
public inline fun <reified T : Any> EffectComponent.getBoundController(
    noinline provider: () -> T,
): BoundEffectController<T> {
    return getBoundController(T::class, provider)
}
