package com.uandcode.effects.core.annotations

import com.uandcode.effects.core.EffectModule
import kotlin.reflect.KClass

/**
 * Mark your class as an implementation of effect interface.
 *
 * Usually you don't need to use this annotation directly, check available plugins
 * for your favorite DI framework instead.
 *
 * Requirements for the annotated implementation class:
 * - it should be a top-level class
 * - it should implement at least one interface
 * - if the class implements more than one interface, a [target] argument
 *   must be specified
 *
 * Requirements for the target interface implemented by the annotated class:
 * - all non-suspend methods should not return any type (except Flow)
 * - suspend methods can return any type
 * - methods can have generic type parameters
 * - target interface and annotated class can't have generic type parameters
 *
 * This annotation generates an [EffectModule] that can be instantiated
 * by using `EffectModuleFactory.createFor<TargetInterface>()` call.
 *
 * Usage example:
 *
 * ```
 * // 1. Define an effect interface
 * interface MyEffects {
 *     fun showToast(message: String)
 * }
 *
 * // 2. Implement the interface and annotate it with @GenerateEffectModule
 * @GenerateEffectModule
 * class MyEffectsImpl(
 *     private val activity: Activity,
 * ) : MyEffects {
 *     override fun showToast(message: String) {
 *         Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
 *     }
 * }
 *
 * // 3. Do not forget to annotate your main application class with @EffectApplication:
 * @EffectApplication
 * class MyApp : Application
 *
 * // 4. Retrieve an instance of EffectModule
 * object EffectModules {
 *     val myEffectsModule = EffectModuleFactory.createFor<MyEffects>()
 * }
 *
 * // 5. Use myEffectsModule.provide() in a component with longer lifecycle:
 * class MyViewModel(
 *     val myEffects: MyEffects = EffectModules.myEffectsModule.provide()
 * ) : ViewModel()
 *
 * // 6. Use myEffectsModule.createController() or myEffectsModule.createBoundController()
 * //    in a component with shorter lifecycle:
 * class MyActivity : AppCompatActivity() {
 *
 *     private val controller: BoundEffectController<MyEffectsImpl> =
 *         EffectModules.myEffectsModule.createBoundController(MyEffectsImpl(this))
 *
 *     override fun onStart() {
 *         super.onStart()
 *         controller.start()
 *     }
 *
 *     override fun onStop() {
 *         super.onStop()
 *         controller.stop()
 *     }
 * }
 * ```
 *
 * @see EffectModule
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public annotation class GenerateEffectModule(

    /**
     * If your class implements only 1 single interface, then
     * that interface is used by default as a target interface.
     *
     * You need to set this parameter only if your class implements
     * 2 or more interfaces.
     */
    public val target: KClass<*> = Any::class,

    /**
     * Set an optional Clean-up method name in the target interface.
     *
     * Please note, the cleanUp() logic cancels only Unit commands. Suspend- and
     * Flow- commands can be cancelled by cancelling a CoroutineScope used
     * for their execution.
     *
     * By default, 'cleanUp' name is used if this parameter is not specified.
     *
     * A cleanUp() method is a special method which should not be abstract and
     * should not return any result. You can add such method to your target interface
     * if you want to manually clear pending unit commands which hasn't been processed yet.
     */
    public val cleanUpMethodName: String = "",

)
