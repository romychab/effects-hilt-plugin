package com.uandcode.effects.core.internal

import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.factories.ProxyEffectFactory
import kotlin.reflect.KClass

internal class EffectClassManager<Effect : Any>(
    private val clazz: KClass<Effect>,
    private val proxyEffectFactory: ProxyEffectFactory,
    private val resourceStore: ObservableResourceStore<Effect> = ObservableResourceStoreImpl(),
) {

    fun provideProxy(): Effect {
        val commandExecutor = CommandExecutorImpl(resourceStore)
        return proxyEffectFactory.createProxy(clazz, commandExecutor)
    }

    fun <T : Effect> createController(): EffectController<T> {
        return EffectControllerImpl(resourceStore)
    }

    fun cleanUp() {
        resourceStore.removeAllObservers()
    }

}
