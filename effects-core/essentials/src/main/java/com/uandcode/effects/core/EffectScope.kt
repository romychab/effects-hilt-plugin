package com.uandcode.effects.core

import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.factories.ProxyEffectFactory
import com.uandcode.effects.stub.api.InvalidEffectSetupException
import kotlin.reflect.KClass

/**
 * Represents a scoped collection of auto-generated proxy effects.
 *
 * You can use a pre-defined global effect scope where all proxy effects
 * are singletons:
 *
 * ```
 * val scope = RootEffectScopes.global
 * ```
 *
 * Alternatively, you can create your own scopes with different lifecycles
 * for your own groups of effect interfaces:
 *
 * ```
 * // store this variable somewhere in a singleton object
 * val singletonScope = RootEffectScopes.empty.createChild(
 *     // the list of target effect interfaces that should be singletons
 *     SingletonEffectInterface1::class,
 *     SingletonEffectInterface2::class,
 * )
 *
 * // create this variable somewhere in Activity ViewModel:
 * val activityRetainedScope = singletonScope.createChild(
 *     // the list of target effect interfaces that should be scoped to
 *     // activity view-model lifecycle
 *     MyActivityRetainedEffectInterface1::class,
 *     MyActivityRetainedEffectInterface2::class,
 * )
 * ```
 */
public interface EffectScope {

    /**
     * Get an auto-generated proxy class of target interface [T].
     * Only interfaces are allowed.
     *
     * @param clazz KClass representing an effect interface (only interfaces are allowed)
     * @throws EffectNotFoundException if the specified [T] interface is not a valid target
     * @throws InvalidEffectSetupException if the library is not setup correctly
     */
    public fun <T : Any> getProxy(clazz: KClass<T>): T

    /**
     * Get a new effect controller which can attach any effect implementation to a target effect interface.
     *
     * @param clazz KClass representing either an effect interface or its annotated child class
     * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
     *                                 it is not a child class of valid interface
     * @throws InvalidEffectSetupException if the library is not setup correctly
     */
    public fun <T : Any> getController(clazz: KClass<T>): EffectController<T>

    /**
     * Create a child [EffectScope] instance that inherits all effects
     * from this parent scope and additionally it can provide proxy effects
     * listed in a [managedInterfaces] argument.
     *
     * @param managedInterfaces the list of target effect interfaces (only interfaces are allowed)
     * @param proxyEffectFactory optional factory for creating proxy implementations for all
     *                           effects listed in [interfaces] argument
     */
    public fun createChild(
        managedInterfaces: ManagedInterfaces,
        proxyEffectFactory: ProxyEffectFactory? = null,
    ): EffectScope

    /**
     * Cancel all pending non-finished calls executed on a generated
     * proxy instance returned by [get] method.
     */
    public fun cleanUp()

}

/**
 * Create a child [EffectScope] instance that inherits all effects
 * from the parent scope and additionally it can provide proxy effects
 * listed in a [interfaces] argument.
 *
 * @param interfaces the list of target effect interfaces (only interfaces are allowed)
 * @param proxyEffectFactory optional factory for creating proxy implementations for all
 *                           effects listed in [interfaces] argument
 */
public fun EffectScope.createChild(
    vararg interfaces: KClass<*>,
    proxyEffectFactory: ProxyEffectFactory? = null
): EffectScope {
    return createChild(
        ManagedInterfaces.ListOf(*interfaces),
        proxyEffectFactory,
    )
}

/**
 * Get an auto-generated proxy class of target interface [T].
 * Only interfaces are allowed.
 *
 * @param T Type representing an effect interface (only interfaces are allowed)
 * @throws EffectNotFoundException if the specified [T] interface is not a valid target
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> EffectScope.getProxy(): T {
    return getProxy(T::class)
}

/**
 * Get a new effect controller which can attach any effect implementation to a target effect interface.
 *
 * @param T Type representing either an effect interface or its annotated child class
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class of valid interface
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> EffectScope.getController(): EffectController<T> {
    return getController(T::class)
}

/**
 * Get a new bound effect controller which can attach an effect implementation
 * created by the [provider] to a target effect interface.
 *
 * @param T Type representing either an effect interface or its annotated child class
 * @param provider Lambda that creates an effect implementation. It is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class of valid interface
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> EffectScope.getBoundController(
    noinline provider: () -> T
): BoundEffectController<T> {
    return getController<T>().bind(provider)
}
