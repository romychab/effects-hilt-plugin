package com.uandcode.effects.core

import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.stub.api.InvalidEffectSetupException
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
 * for your own groups of effect interfaces:
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
     * Create a child [EffectComponent] instance that inherits all effects
     * from this parent component and additionally it can provide proxy effects
     * listed in a [interfaces] argument.
     *
     * @param interfaces the list of target effect interfaces (only interfaces are allowed)
     * @param proxyEffectFactory optional factory for creating proxy implementations for all
     *                           effects listed in [interfaces] argument
     */
    public fun createChild(
        interfaces: EffectInterfaces,
        proxyEffectFactory: ProxyEffectFactory? = null,
    ): EffectComponent

    /**
     * Cancel all pending non-finished calls executed on a generated
     * proxy instance returned by [get] method.
     */
    public fun cleanUp()

}

/**
 * @see EffectComponent.getProxy
 */
public inline fun <reified T : Any> EffectComponent.getProxy(): T {
    return getProxy(T::class)
}

/**
 * @see EffectComponent.getController
 */
public inline fun <reified T : Any> EffectComponent.getController(): EffectController<T> {
    return getController(T::class)
}
