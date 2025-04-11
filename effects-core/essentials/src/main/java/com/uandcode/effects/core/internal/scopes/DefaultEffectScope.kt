package com.uandcode.effects.core.internal.scopes

import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.factories.ProxyEffectFactory
import com.uandcode.effects.core.internal.CommandExecutorImpl
import com.uandcode.effects.core.internal.EffectControllerImpl
import com.uandcode.effects.core.internal.ObservableResourceStore
import com.uandcode.effects.core.internal.ObservableResourceStoreImpl
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class DefaultEffectScope(
    private val managedInterfaces: ManagedInterfaces,
    private val proxyEffectFactory: ProxyEffectFactory,
    private val parent: EffectScope?,
) : EffectScope, HasManagedResourceStores {

    private val resourceStores = mutableMapOf<KClass<*>, ObservableResourceStore<*>>()

    override fun <T : Any> getController(clazz: KClass<T>): EffectController<T> {
        val targetInterfaces = proxyEffectFactory.findTargetInterfaces(clazz)
            .toMutableSet()
        val stores = targetInterfaces.map {
            getResourceStore(it) ?: throwEffectNotFoundException(clazz)
        }
        return EffectControllerImpl(stores) as EffectController<T>
    }

    override fun <T : Any> getProxy(clazz: KClass<T>): T {
        val store = getResourceStore(clazz)
            as? ObservableResourceStore<T>
            ?: throwEffectNotFoundException(clazz)
        val commandExecutor = CommandExecutorImpl(store)
        return proxyEffectFactory.createProxy(clazz, commandExecutor)
    }

    override fun createChild(
        managedInterfaces: ManagedInterfaces,
        proxyEffectFactory: ProxyEffectFactory?
    ): EffectScope {
        return DefaultEffectScope(
            managedInterfaces = managedInterfaces,
            proxyEffectFactory = proxyEffectFactory ?: this.proxyEffectFactory,
            parent = this,
        )
    }

    override fun cleanUp() {
        resourceStores.values.forEach {
            it.removeAllObservers()
        }
    }

    override fun getResourceStore(interfaceClass: KClass<*>): ObservableResourceStore<*>? {
        return if (managedInterfaces.contains(interfaceClass)) {
            resourceStores[interfaceClass] ?: ObservableResourceStoreImpl<Any>().also {
                resourceStores[interfaceClass] = it
            }
        } else {
            (parent as? HasManagedResourceStores)?.getResourceStore(interfaceClass)
        }
    }

    private fun throwEffectNotFoundException(clazz: KClass<*>): Nothing {
        throw EffectNotFoundException(clazz, proxyEffectFactory.proxyConfiguration)
    }

}
