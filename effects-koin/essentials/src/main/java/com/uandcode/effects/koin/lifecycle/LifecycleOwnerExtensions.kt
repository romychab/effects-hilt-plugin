package com.uandcode.effects.koin.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.exceptions.EffectNotFoundException
import com.uandcode.effects.koin.annotations.KoinEffect
import com.uandcode.effects.koin.controller.getBoundEffectController
import com.uandcode.effects.stub.api.InvalidEffectSetupException
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.KoinScopeComponent
import org.koin.core.context.GlobalContext

/**
 * Attach an effect implementation of the type [T] created by the [effectProvider]
 * argument to its target interface. The class [T] must be annotated
 * with a [KoinEffect] annotation.
 *
 * The effect implementation is automatically attached to the target interface when
 * the lifecycle managed by [LifecycleOwner] is started, and afterwards it is detached
 * when the lifecycle is stopped.
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
 * @KoinEffect
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
 * @param effectProvider The function that creates an instance of the effect implementation.
 *                       This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [KoinEffect]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> LifecycleOwner.lazyEffect(
    noinline effectProvider: () -> T,
): KoinEffectDelegate<T> {
    val controllerProvider: () -> BoundEffectController<T> = {
        when (this) {
            is KoinScopeComponent -> getBoundEffectController(effectProvider)
            is KoinComponent -> getBoundEffectController(effectProvider)
            is AndroidScopeComponent -> getBoundEffectController(effectProvider)
            else -> GlobalContext.get().getBoundEffectController(effectProvider)
        }
    }
    return KoinEffectDelegateImpl(controllerProvider)
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
 * The class [T] must be annotated with a [KoinEffect] annotation. The effect implementation is automatically
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
 * @KoinEffect
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
 * @param effectProvider The function that creates an instance of the effect implementation.
 *                       This function is called lazily and only once.
 * @throws EffectNotFoundException if the specified [T] type is not a valid target interface or
 *                                 it is not a child class annotated with [EffectClass]
 * @throws InvalidEffectSetupException if the library is not setup correctly
 */
public inline fun <reified T : Any> LifecycleOwner.initEffect(
    noinline effectProvider: () -> T
) {
    lazyEffect(effectProvider)
}
