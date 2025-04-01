package com.uandcode.effects.core.lifecycle.internal

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.lifecycle.LazyEffectDelegate
import kotlin.reflect.KProperty

@PublishedApi
internal class LazyEffectDelegateImpl<T>(
    lifecycleOwner: LifecycleOwner,
    private val controller: BoundEffectController<T>,
) : LazyEffectDelegate<T>, DefaultLifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return controller.effectImplementation
    }

    override fun onStart(owner: LifecycleOwner) {
        controller.start()
    }

    override fun onStop(owner: LifecycleOwner) {
        controller.stop()
    }

}
