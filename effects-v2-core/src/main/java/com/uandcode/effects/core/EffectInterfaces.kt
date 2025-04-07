package com.uandcode.effects.core

import com.uandcode.effects.core.factories.ProxyEffectFactory
import com.uandcode.effects.core.internal.EffectClassManager
import kotlin.reflect.KClass

/**
 * The list of effect interfaces managed by an [EffectScope].
 *
 * @see EffectInterfaces.Everything
 * @see EffectInterfaces.ListOf
 */
public sealed class EffectInterfaces {

    internal abstract fun createEffectManager(
        interfaceClass: KClass<*>,
        proxyEffectFactory: ProxyEffectFactory,
    ): EffectClassManager<out Any>?

    /**
     * The [EffectInterfaces] instance that matches all possible interfaces.
     */
    public data object Everything : EffectInterfaces() {

        override fun createEffectManager(
            interfaceClass: KClass<*>,
            proxyEffectFactory: ProxyEffectFactory
        ): EffectClassManager<out Any> {
            return EffectClassManager(interfaceClass, proxyEffectFactory) as EffectClassManager<Any>
        }

    }

    /**
     * Only effects listed in [interfaces] argument are managed by the scope.
     */
    public class ListOf(
        private vararg val interfaces: KClass<*>
    ) : EffectInterfaces() {

        override fun createEffectManager(
            interfaceClass: KClass<*>,
            proxyEffectFactory: ProxyEffectFactory
        ): EffectClassManager<out Any>? {
            return if (interfaces.contains(interfaceClass)) {
                EffectClassManager(interfaceClass, proxyEffectFactory)
            } else {
                return null
            }
        }

    }

}