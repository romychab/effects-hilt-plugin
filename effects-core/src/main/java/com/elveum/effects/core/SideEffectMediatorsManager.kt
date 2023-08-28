package com.elveum.effects.core

import com.elveum.effects.core.actors.SideEffectImplementation
import com.elveum.effects.core.actors.SideEffectMediator
import com.elveum.effects.core.di.SideEffectsMediatorScope
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

/**
 * For internal usage.
 * This class activates all mediators when activity is started and
 * deactivates them when activity is stopped.
 */
@ActivityRetainedScoped
class SideEffectMediatorsManager @Inject constructor(
    private val mediators: Map<String, @JvmSuppressWildcards SideEffectMediator<Any>>,
    @SideEffectsMediatorScope private val scope: CoroutineScope
) {

    fun onStart(implementations: Set<SideEffectImplementation>) {
        implementations.forEach { impl ->
            val mediator = mediators[impl.target]
            mediator?.target = impl.instance
        }
    }

    fun onStop() {
        mediators.values.forEach { mediator ->
            mediator.target = null
        }
    }

    fun destroy() {
        scope.cancel()
    }
}