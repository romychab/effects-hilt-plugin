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
internal class SideEffectMediatorsManager @Inject constructor(
    private val mediators: Map<String, @JvmSuppressWildcards SideEffectMediator<Any>>,
    @SideEffectsMediatorScope private val scope: CoroutineScope
) {

    private var started = false

    fun onStart(implementations: Set<SideEffectImplementation>) {
        if (started) return
        started = true
        implementations.forEach { impl ->
            val mediator = mediators[impl.target]
            mediator?.target = impl.instance
        }
    }

    fun onStop() {
        if (!started) return
        started = false
        mediators.values.forEach { mediator ->
            mediator.target = null
        }
    }

    fun destroy() {
        scope.cancel()
    }
}