package com.uandcode.effects.core.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.RootEffectScopes
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.effects.core.bind
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.core.getController
import com.uandcode.effects.stub.api.InvalidEffectSetupException

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
 * @param scope The [EffectScope] that is used to retrieve an instance
 *                  of the effect interface to which the effect implementation
 *                  will be attached. By default, [RootEffectScopes.global] is used.
 * @param effectProvider The function that creates an instance of the effect implementation.
 *                       This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [EffectClass]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> LifecycleOwner.lazyEffect(
    scope: EffectScope = RootEffectScopes.global,
    noinline effectProvider: () -> T,
): EffectLifecycleDelegate<T> {
    val controller = scope.getController<T>().bind(effectProvider)
    return EffectLifecycleDelegateImpl(controller)
        .also(lifecycle::addObserver)
}

/**
 * Attach an effect implementation of a type [T] created by a [effectProvider] to the
 * target interface.
 *
 * This method works in the same way as [lazyEffect], but it does not return the created effect
 * instance itself. It may be useful, if you only need to attach the effect implementation without
 * further interaction with the created instance.
 *
 * The class [T] must be annotated with an [EffectClass] annotation. The effect implementation is automatically attached when the lifecycle
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
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         initEffect { ToastMessagesImpl(context = this) }
 *     }
 * }
 * ```
 *
 * @param scope The [EffectScope] that is used to retrieve an instance
 *                  of the effect interface to which the effect implementation
 *                  will be attached. By default, [RootEffectScopes.global] is used.
 * @param effectProvider The function that creates an instance of the effect implementation.
 *                       This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [EffectClass]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> LifecycleOwner.initEffect(
    scope: EffectScope = RootEffectScopes.global,
    noinline effectProvider: () -> T
) {
    lazyEffect(scope, effectProvider)
}
