package com.elveum.effects.core.v2.impl

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.elveum.effects.core.v2.BoundEffectController
import com.elveum.effects.core.v2.LazyEffectDelegate
import kotlin.reflect.KProperty

public class LazyEffectDelegateImpl<T>(
    lifecycle: Lifecycle,
    private val effectControllerProvider: () -> BoundEffectController<T>,
) : LazyEffectDelegate<T>, DefaultLifecycleObserver {

    private var boundEffectController: BoundEffectController<T>? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getEffectController().effectImplementation
    }

    override fun onStart(owner: LifecycleOwner) {
        getEffectController().start()
    }

    override fun onStop(owner: LifecycleOwner) {
        getEffectController().stop()
    }

    private fun getEffectController(): BoundEffectController<T> {
        return boundEffectController ?: effectControllerProvider().also { newController ->
            this.boundEffectController = newController
        }
    }

}