package com.uandcode.effects.core.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.RootEffectComponents
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.effects.core.bind
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.getController
import com.uandcode.effects.stub.api.InvalidEffectSetupException
import com.uandcode.effects.core.lifecycle.internal.EffectLifecycleDelegateImpl

/**
 * Attach an effect implementation of a type [T] created by a [effectProvider] to the
 * target interface. The class [T] must be annotated with an [EffectClass] annotation.
 *
 * The effect implementation is automatically attached when the lifecycle
 * managed by [LifecycleOwner] is started and detached when it is stopped.
 *
 * Please note that the [effectProvider] function is called lazily and only once.
 *
 * Usage example:
 *
 * ```
 * interface ToastMessages {
 *    fun showToast(message: String)
 * }
 *
 * @EffectClass
 * class ToastMessagesImpl(private val context: Context) : ToastMessages {
 *     override fun showToast(message: String) {
 *         Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
 *     }
 * }
 *
 * class MyActivity : AppCompatActivity() {
 *     private val toastMessages by lazyEffect {
 *         ToastMessagesImpl(context = this)
 *     }
 * }
 * ```
 *
 * @param component The [EffectComponent] that is used to retrieve an instance
 *                  of the effect interface to which the effect implementation
 *                  will be attached. By default, [RootEffectComponents.global] is used.
 * @param effectProvider The function that creates an instance of the effect implementation.
 *                       This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [EffectClass]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> LifecycleOwner.lazyEffect(
    component: EffectComponent = RootEffectComponents.global,
    noinline effectProvider: () -> T,
): EffectLifecycleDelegate<T> {
    return EffectLifecycleDelegate.lazy {
        val controller = component.getController<T>().bind(effectProvider)
        EffectLifecycleDelegateImpl(
            lifecycleOwner = this,
            controller = controller,
        )
    }
}
