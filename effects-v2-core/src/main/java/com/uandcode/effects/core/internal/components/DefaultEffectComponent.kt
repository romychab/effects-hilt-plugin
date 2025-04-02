package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.internal.EffectFactory
import com.uandcode.effects.core.internal.ProxyEffectStoreProvider.getGeneratedProxyEffectStore
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class DefaultEffectComponent(
    targetInterfaceClasses: Set<KClass<*>>,
    private val parent: EffectComponent = EmptyEffectComponent,
) : AbstractEffectComponent() {

    private val factories: Map<KClass<*>, Lazy<EffectFactory<*>>> =
        targetInterfaceClasses.associateWith { clazz ->
            lazy {
                EffectFactory(clazz)
            }
        }

    override fun <T : Any> getController(clazz: KClass<T>): EffectController<T> {
        return getFactory(clazz)?.createController()
            ?: parent.getController(clazz)
    }

    override fun <T : Any> getBoundController(
        clazz: KClass<T>,
        provider: () -> T
    ): BoundEffectController<T> {
        return getFactory(clazz)?.createBoundController(provider)
            ?: parent.getBoundController(clazz, provider)
    }

    override fun <T : Any> get(clazz: KClass<T>): T {
        return (factories[clazz]?.value as? EffectFactory<T>)
            ?.provideProxy()
            ?: parent.get(clazz)
    }

    override fun cleanUp() {
        factories.values.filter { it.isInitialized() }
            .forEach {
                it.value.cleanUp()
            }
    }

    private fun <T : Any> getFactory(clazz: KClass<T>): EffectFactory<T>? {
        val interfaceClass = getGeneratedProxyEffectStore().findTargetInterface(clazz)
        return factories[interfaceClass]?.value as? EffectFactory<T>
    }

}
