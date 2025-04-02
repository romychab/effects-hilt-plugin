package com.uandcode.effects.core.internal

import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.EffectController
import com.uandcode.effects.core.ObservableResourceStore
import com.uandcode.effects.core.internal.ProxyEffectStoreProvider.getGeneratedProxyEffectStore
import kotlin.reflect.KClass

internal class EffectFactory<Effect : Any>(
    private val clazz: KClass<Effect>,
    private val resourceStore: ObservableResourceStore<Effect> = ObservableResourceStoreImpl(),
) {

    fun provideProxy(): Effect {
        val commandExecutor = CommandExecutorImpl(resourceStore)
        return getGeneratedProxyEffectStore().createProxy(clazz, commandExecutor)
    }

    fun <T : Effect> createController(): EffectController<T> {
        return EffectControllerImpl(resourceStore)
    }

    fun <T : Effect> createBoundController(
        boundImplementationProvider: () -> T
    ): BoundEffectController<T> {
        val controller = EffectControllerImpl<T>(resourceStore)
        return BoundEffectControllerImpl(controller, boundImplementationProvider)
    }

    fun cleanUp() {
        resourceStore.removeAllObservers()
    }

}
