package com.uandcode.effects.core.internal.scopes

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.factories.ProxyEffectFactory
import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.internal.ObservableResourceStore
import kotlin.reflect.KClass

internal class LazyEffectScope(
    private val provider: () -> EffectScope,
) : EffectScope, HasManagedResourceStores {

    private val origin by lazy { provider() }

    override fun <T : Any> getProxy(clazz: KClass<T>): T {
        return origin.getProxy(clazz)
    }

    override fun <T : Any> getController(clazz: KClass<T>): EffectController<T> {
        return origin.getController(clazz)
    }

    override fun createChild(
        managedInterfaces: ManagedInterfaces,
        proxyEffectFactory: ProxyEffectFactory?
    ): EffectScope {
        return origin.createChild(
            managedInterfaces = managedInterfaces,
            proxyEffectFactory = proxyEffectFactory,
        )
    }

    override fun cleanUp() {
        origin.cleanUp()
    }

    override fun getResourceStore(interfaceClass: KClass<*>): ObservableResourceStore<*>? {
        return (origin as? HasManagedResourceStores)?.getResourceStore(interfaceClass)
    }

}
