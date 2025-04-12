package com.uandcode.effects.hilt.annotations

import kotlin.reflect.KClass

/**
 * Mark your class as an implementation of an effect interface.
 *
 * This annotation generates proxy classes for all interfaces listed
 * in the [targets] parameter. If the parameter is not specified, all
 * interfaces implemented by the class are considered as target interfaces.
 *
 * The generated proxies can be injected into objects that have longer
 * lifecycle than your implementation's lifecycle.
 *
 * Requirements for the annotated class:
 * - it should be a top-level class
 * - it should implement at least one interface
 *
 * Requirements for the target interface implemented by the annotated class:
 * - all non-suspend methods should not return any type (except Flow)
 * - suspend methods can return any type
 * - methods can have generic type parameters
 * - target interface and annotated class can't have generic type parameters
 *
 * Usage example:
 *
 * ```
 * // 1. Define an effect interface
 * interface MyEffects {
 *     fun showToast(message: String)
 * }
 *
 * // 2. Implement the interface and annotate it with @HiltEffect
 * @HiltEffect
 * class MyEffectsImpl(
 *     private val activity: Activity,
 * ) : MyEffects {
 *     override fun showToast(message: String) {
 *         Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
 *     }
 * }
 *
 * // 3. Inject the interface into your ViewModel
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     val myEffects: MyEffects
 * ) : ViewModel()
 *
 * // 4. Use lazyEffect delegate in a object with shorter lifecycle:
 * class MyActivity : AppCompatActivity() {
 *
 *     private val myEffectsImpl by lazyEffect {
 *         MyEffectsImpl(activity = this)
 *     }
 *
 * }
 *
 * // As a result, MyEffectsImpl object will be attached to the MyEffects proxy injected to the
 * // ViewModel when the Activity is at least in STARTED state (between onStart/onStop calls).
 * ```
 *
 * The target interface may optionally extend AutoCloseable interface
 * and provide a default implementation of the `close()` method:
 *
 * ```
 * interface MyEffects : AutoCloseable {
 *     override fun close() = Unit
 * }
 * ```
 *
 * Calling `close()` cancels all pending calls that hasn't been finished or
 * delivered yet from a proxy to an effect implementation.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class HiltEffect(
    public val targets: Array<KClass<*>> = [],
    public val installIn: KClass<*> = Any::class,
)
