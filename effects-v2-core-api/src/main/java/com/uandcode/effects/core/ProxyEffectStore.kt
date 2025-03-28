package com.uandcode.effects.core

import kotlin.reflect.KClass

/**
 * For internal usage.
 *
 * Public modifier is required because this interface is used in auto-generated code.
 */
public interface ProxyEffectStore {

    /**
     * Collection of KClass objects for all target interfaces gathered from
     * all processed annotations.
     */
    public val allTargetInterfaces: Set<KClass<*>>

    /**
     * Create a proxy class of the specified effect interface [T].
     * @throws IllegalStateException if annotations are not used correctly or dependencies are not added to the build.gradle
     * @throws IllegalArgumentException if there is no effect class for the specified interface [T]
     */
    public fun <T : Any> createProxy(clazz: KClass<T>, commandExecutor: CommandExecutor<T>): T

    /**
     * Find a target interface KClass instance by KClass instance of a child class.
     */
    public fun findTargetInterface(clazz: KClass<*>): KClass<*>?

}
