package com.uandcode.effects.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.BoundEffectController
import kotlin.reflect.KProperty

@PublishedApi
internal class EffectLifecycleDelegateImpl<T>(
    lifecycleOwner: LifecycleOwner,
    private val controller: BoundEffectController<T>,
) : EffectLifecycleDelegate<T>, DefaultLifecycleObserver {

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
