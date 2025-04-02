package com.uandcode.effects.stub.api

import kotlin.reflect.KClass

/**
 * For internal usage.
 */
public interface ProxyEffectStore {

    /**
     * @see ProxyConfiguration
     */
    public val proxyConfiguration: ProxyConfiguration

    /**
     * Collection of KClass objects for all target interfaces gathered from
     * all processed annotations.
     */
    public val allTargetInterfaces: Set<KClass<*>>

    /**
     * Create a proxy class of the specified effect interface [T].
     */
    public fun <T : Any> createProxy(clazz: KClass<T>, proxyDependency: ProxyDependency): T

    /**
     * Find a target interface KClass instance by KClass instance of a child class.
     */
    public fun findTargetInterface(clazz: KClass<*>): KClass<*>?

}
