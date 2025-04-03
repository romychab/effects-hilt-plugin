package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.ProxyEffectFactory
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.internal.EffectClassManager
import com.uandcode.effects.core.EffectInterfaces
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class DefaultEffectComponent(
    private val interfaces: EffectInterfaces,
    private val proxyEffectFactory: ProxyEffectFactory,
    private val parent: EffectComponent?,
) : EffectComponent {

    private val effectClassManagers = mutableMapOf<KClass<*>, EffectClassManager<out Any>>()

    override fun <T : Any> getController(clazz: KClass<T>): EffectController<T> {
        return getEffectClassManager(clazz)
            ?.createController()
            ?: parent?.getController(clazz)
            ?: throwEffectNotFoundException(clazz)
    }

    override fun <T : Any> getProxy(clazz: KClass<T>): T {
        return getEffectClassManager(clazz)
            ?.provideProxy()
            ?: parent?.getProxy(clazz)
            ?: throwEffectNotFoundException(clazz)
    }

    override fun createChild(
        interfaces: EffectInterfaces,
        proxyEffectFactory: ProxyEffectFactory?
    ): EffectComponent {
        return DefaultEffectComponent(
            interfaces = interfaces,
            proxyEffectFactory = proxyEffectFactory ?: this.proxyEffectFactory,
            parent = this,
        )
    }

    override fun cleanUp() {
        effectClassManagers.values.forEach(EffectClassManager<*>::cleanUp)
    }

    private fun <T : Any> getEffectClassManager(clazz: KClass<T>): EffectClassManager<T>? {
        val interfaceClass = proxyEffectFactory.findTargetInterface(clazz)
        return effectClassManagers[interfaceClass] as? EffectClassManager<T>
            ?: createNewEffectManager(interfaceClass)
    }

    private fun <T : Any> createNewEffectManager(interfaceClass: KClass<*>): EffectClassManager<T>? {
        return interfaces.createEffectManager(
            interfaceClass = interfaceClass,
            proxyEffectFactory = proxyEffectFactory,
        )?.also {
            effectClassManagers[interfaceClass] = it
        } as? EffectClassManager<T>
    }

    private fun throwEffectNotFoundException(clazz: KClass<*>): Nothing {
        throw EffectNotFoundException(clazz, proxyEffectFactory.proxyConfiguration)
    }

}
