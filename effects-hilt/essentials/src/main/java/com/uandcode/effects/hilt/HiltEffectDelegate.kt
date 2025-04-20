package com.uandcode.effects.hilt

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.BoundEffectController
import kotlin.reflect.KProperty

public interface HiltEffectDelegate<T> {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

@PublishedApi
internal class HiltEffectDelegateImpl<T>(
    controllerProvider: () -> BoundEffectController<T>,
) : HiltEffectDelegate<T>, DefaultLifecycleObserver {

    private val controller: BoundEffectController<T> by lazy(controllerProvider)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return controller.effectImplementation
            ?: throw IllegalStateException("Property '${property.name}' is accessed before Hilt injection.")
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        controller.start()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        controller.stop()
    }

}
