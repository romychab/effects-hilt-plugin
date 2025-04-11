package com.uandcode.effects.core

import kotlin.reflect.KClass

/**
 * The list of effect interfaces managed by an [EffectScope].
 *
 * @see ManagedInterfaces.Everything
 * @see ManagedInterfaces.ListOf
 */
public sealed class ManagedInterfaces {

    public abstract fun contains(interfaceClass: KClass<*>): Boolean

    /**
     * The [ManagedInterfaces] instance that matches all possible interfaces.
     */
    public data object Everything : ManagedInterfaces() {
        override fun contains(interfaceClass: KClass<*>): Boolean {
            return true
        }
    }

    /**
     * Only effects listed in `interfaces` argument are managed by the scope.
     */
    public class ListOf(
        vararg interfaces: KClass<*>
    ) : ManagedInterfaces() {

        private val managedInterfaces: Set<KClass<*>> = interfaces.toSet()

        override fun contains(interfaceClass: KClass<*>): Boolean {
            return managedInterfaces.contains(interfaceClass)
        }
    }

}