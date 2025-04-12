package com.uandcode.effects.hilt

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.BoundEffectController
import kotlin.reflect.KProperty

public interface HiltEffectDelegate<T> {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

@PublishedApi
internal class HiltEffectDelegateImpl<T>(
    private val controllerProvider: () -> BoundEffectController<T>,
    lifecycle: Lifecycle,
) : HiltEffectDelegate<T>, DefaultLifecycleObserver {

    private var controller: BoundEffectController<T>? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return controller?.effectImplementation
            ?: throw IllegalStateException("Property '${property.name}' is accessed before Hilt injection.")
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        controller = controllerProvider()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        controller?.start()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        controller?.stop()
    }

}
