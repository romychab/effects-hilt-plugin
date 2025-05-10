@file:OptIn(KoinInternalApi::class)

package com.uandcode.effects.koin.internal

import androidx.lifecycle.ViewModel
import com.uandcode.effects.core.EffectProxyMarker
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ResolutionContext
import org.koin.core.scope.Scope

internal class InstanceFactoryInterceptor<T>(
    private val argStack: ConstructorArgStack,
    private val origin: InstanceFactory<T>,
) : InstanceFactory<T>(origin.beanDefinition) {

    private var state: State<T> = Enabled()

    override fun get(context: ResolutionContext): T {
        return state.get(context)
    }

    override fun drop(scope: Scope?) {
        origin.drop(scope)
    }

    override fun dropAll() {
        origin.dropAll()
    }

    override fun isCreated(context: ResolutionContext?): Boolean {
        return origin.isCreated(context)
    }

    private interface State<T> {
        fun get(context: ResolutionContext): T
    }

    private inner class Enabled : State<T> {

        @Synchronized
        override fun get(context: ResolutionContext): T {
            val isInitialInstance = argStack.count() == 0
            try {
                val constructorArg = ConstructorArg()
                argStack.push(constructorArg)
                val createdInstance = origin.get(context)
                when (createdInstance) {
                    is ViewModel -> handleViewModel(createdInstance, constructorArg)
                    is EffectProxyMarker -> handleEffect(createdInstance)
                    else -> disable()
                }
                return createdInstance
            } finally {
                if (isInitialInstance) {
                    argStack.clear()
                }
            }
        }

        private fun handleViewModel(
            viewModel: ViewModel,
            constructorArg: ConstructorArg,
        ) {
            if (viewModel.getCloseable<AutoCloseable>(KOIN_CLOSEABLE_KEY) != null) return
            val args = argStack.popTo(constructorArg)
            viewModel.addCloseable(KOIN_CLOSEABLE_KEY) {
                args.forEach(AutoCloseable::close)
            }
            argStack.pop()
        }

        private fun handleEffect(closeable: AutoCloseable) {
            argStack.peek()?.assignCloseable(closeable)
        }

        private fun disable() {
            state = Disabled()
        }

    }

    private inner class Disabled : State<T> {
        override fun get(context: ResolutionContext): T = origin.get(context)
    }

    private companion object {
        const val KOIN_CLOSEABLE_KEY = "com.uandcode.effects.koin.closeable"
    }

}

internal fun KoinApplication.overrideInstances(
    constructorArgStack: ConstructorArgStack = ConstructorArgStack()
) {
    val allInstances = koin.instanceRegistry.instances as MutableMap
    allInstances.keys.toList().forEach { key ->
        allInstances[key]?.let { instanceFactory ->
            if (instanceFactory !is InstanceFactoryInterceptor) {
                allInstances[key] = InstanceFactoryInterceptor(constructorArgStack, instanceFactory)
            }
        }
    }
}
