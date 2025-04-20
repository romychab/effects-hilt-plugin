package com.uandcode.effects.hilt

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.bind
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.lifecycle.lazyEffect
import com.uandcode.effects.hilt.annotations.HiltEffect
import com.uandcode.effects.stub.api.InvalidEffectSetupException

/**
 * Attach an effect implementation of a type [T] created by a [effectProvider] argument to the
 * target interface. The class [T] must be annotated with an [HiltEffect] annotation.
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
 * @HiltEffect
 * class ToastMessagesImpl(private val context: Context) : ToastMessages {
 *     override fun showToast(message: String) {
 *         Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
 *     }
 * }
 *
 * @AndroidEntryPoint
 * class MyActivity : AppCompatActivity() {
 *     private val toastMessages by lazyEffect {
 *         ToastMessagesImpl(context = this)
 *     }
 * }
 * ```
 *
 * @param effectProvider The function that creates an instance of the effect implementation.
 *                       This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [HiltEffect]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> ComponentActivity.lazyEffect(
    noinline effectProvider: () -> T,
): HiltEffectDelegate<T> {
    return lazyEffect(
        scopeProvider = { getEffectEntryPoint().getEffectScope() },
        effectProvider = effectProvider,
    )
}

/**
 * Attach an effect implementation of a type [T] created by a [effectProvider] argument to the
 * target interface. The class [T] must be annotated with an [HiltEffect] annotation.
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
 * @HiltEffect
 * class ToastMessagesImpl(private val context: Context) : ToastMessages {
 *     override fun showToast(message: String) {
 *         Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
 *     }
 * }
 *
 * @AndroidEntryPoint
 * class MyActivity : AppCompatActivity() {
 *     private val toastMessages by lazyEffect {
 *         ToastMessagesImpl(context = this)
 *     }
 * }
 * ```
 *
 * @param effectProvider The function that creates an instance of the effect implementation.
 *                       This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [HiltEffect]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> Fragment.lazyEffect(
    noinline effectProvider: () -> T,
): HiltEffectDelegate<T> {
    return lazyEffect(
        scopeProvider = { getEffectEntryPoint().getEffectScope() },
        effectProvider = effectProvider,
    )
}

/**
 * Attach an effect implementation of a type [T] created by a [effectProvider] argument to the
 * target interface.
 *
 * This method works in the same way as [lazyEffect], but it does not return the created effect
 * instance itself. It may be useful, if you only need to attach the effect implementation without
 * further interaction with the created instance.
 *
 * The class [T] must be annotated with an [HiltEffect] annotation. The effect implementation is automatically
 * attached when the lifecycle managed by [LifecycleOwner] is started and detached when it is stopped.
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
 * @HiltEffect
 * class ToastMessagesImpl(private val context: Context) : ToastMessages {
 *     override fun showToast(message: String) {
 *         Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
 *     }
 * }
 *
 * @AndroidEntryPoint
 * class MyActivity : AppCompatActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         initEffect { ToastMessagesImpl(context = this) }
 *     }
 * }
 * ```
 *
 * @param effectProvider The function that creates an instance of the effect implementation.
 *                       This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [HiltEffect]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> ComponentActivity.initEffect(
    noinline effectProvider: () -> T
) {
    lazyEffect(effectProvider)
}

/**
 * Attach an effect implementation of a type [T] created by a [effectProvider] argument to the
 * target interface.
 *
 * This method works in the same way as [lazyEffect], but it does not return the created effect
 * instance itself. It may be useful, if you only need to attach the effect implementation without
 * further interaction with the created instance.
 *
 * The class [T] must be annotated with an [HiltEffect] annotation. The effect implementation is automatically
 * attached when the lifecycle managed by [LifecycleOwner] is started and detached when it is stopped.
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
 * @HiltEffect
 * class ToastMessagesImpl(private val context: Context) : ToastMessages {
 *     override fun showToast(message: String) {
 *         Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
 *     }
 * }
 *
 * @AndroidEntryPoint
 * class MyActivity : AppCompatActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         initEffect { ToastMessagesImpl(context = this) }
 *     }
 * }
 * ```
 *
 * @param effectProvider The function that creates an instance of the effect implementation.
 *                       This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [HiltEffect]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> Fragment.initEffect(
    noinline effectProvider: () -> T
) {
    lazyEffect(effectProvider)
}

@PublishedApi
internal inline fun <reified T : Any> LifecycleOwner.lazyEffect(
    crossinline scopeProvider: () -> EffectScope,
    noinline effectProvider: () -> T,
): HiltEffectDelegate<T> {
    val delegate = HiltEffectDelegateImpl(
        controllerProvider = {
            scopeProvider().getController(T::class).bind(effectProvider)
        }
    )
    lifecycle.addObserver(delegate)
    return delegate
}
