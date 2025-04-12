package com.uandcode.effects.hilt

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.bind
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.hilt.annotations.HiltEffect
import com.uandcode.effects.stub.api.InvalidEffectSetupException

/**
 * Attach an effect implementation of a type [T] created by a [provider] argument to the
 * target interface. The class [T] must be annotated with an [HiltEffect] annotation.
 *
 * The effect implementation is automatically attached when the lifecycle
 * managed by [LifecycleOwner] is started and detached when it is stopped.
 *
 * Please note that the [provider] function is called lazily and only once.
 *
 * Usage example:
 *
 * ```
 * interface ToastMessages {
 *    fun showToast(message: String)
 * }
 *
 * @HiltEffect
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
 * @param provider The function that creates an instance of the effect implementation.
 *                 This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [EffectClass]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> ComponentActivity.lazyEffect(
    noinline provider: () -> T,
): HiltEffectDelegate<T> {
    return lazyEffect(
        scopeProvider = { getEffectEntryPoint().getEffectScope() },
        effectProvider = provider,
    )
}

/**
 * Attach an effect implementation of a type [T] created by a [provider] argument to the
 * target interface. The class [T] must be annotated with an [HiltEffect] annotation.
 *
 * The effect implementation is automatically attached when the lifecycle
 * managed by [LifecycleOwner] is started and detached when it is stopped.
 *
 * Please note that the [provider] function is called lazily and only once.
 *
 * Usage example:
 *
 * ```
 * interface ToastMessages {
 *    fun showToast(message: String)
 * }
 *
 * @HiltEffect
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
 * @param provider The function that creates an instance of the effect implementation.
 *                 This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [EffectClass]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> Fragment.lazyEffect(
    noinline provider: () -> T,
): HiltEffectDelegate<T> {
    return lazyEffect(
        scopeProvider = { getEffectEntryPoint().getEffectScope() },
        effectProvider = provider,
    )
}

@PublishedApi
internal inline fun <reified T : Any> LifecycleOwner.lazyEffect(
    crossinline scopeProvider: () -> EffectScope,
    noinline effectProvider: () -> T,
): HiltEffectDelegate<T> {
    return HiltEffectDelegateImpl(
        lifecycle = this.lifecycle,
        controllerProvider = {
            scopeProvider().getController(T::class).bind(effectProvider)
        }
    )
}
