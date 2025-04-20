package com.uandcode.effects.koin.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.BoundEffectController
import kotlin.reflect.KProperty

public interface KoinEffectDelegate<T> {
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): T
}

@PublishedApi
internal class KoinEffectDelegateImpl<T>(
    controllerProvider: () -> BoundEffectController<T>,
) : KoinEffectDelegate<T>, DefaultLifecycleObserver {

    private val controller by lazy(controllerProvider)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return controller.effectImplementation
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
