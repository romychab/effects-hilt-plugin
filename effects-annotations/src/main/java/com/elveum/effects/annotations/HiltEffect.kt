package com.elveum.effects.annotations

import kotlin.reflect.KClass

/**
 * Mark your class as an implementation of effect interface.
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
 * - target interface can't have generic type parameters
 *
 * Lifecycle of all effect interfaces which are injected to your view-models
 * (both for activities and fragments) are tied to the Hilt `ActivityRetainedComponent`
 * by default.
 *
 * ```
 * time -->
 * +------------------------------------------------------------------+
 * | Effect interface (retains across configuration changes)          |
 * +------------------+-----------+-----+----------+------------------+
 * | Activity active  | onDestroy | ... | onCreate | Activity rotated |
 * +------------------+---------- +-----+----------+------------------+
 * | Effect impl. 1               |     | Effect impl. 2              |
 * +------------------+---------- +     +-----------------------------+
 * ```
 *
 * All suspend/flow methods of the effect implementation are executed in
 * the coroutine scope which automatically cancels suspend methods being
 * executed when the activity is going to be stopped. Afterwards, when
 * the activity restarts, all cancelled non-finished suspend methods
 * are re-executed again.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public annotation class HiltEffect(

    @Deprecated(
        message = "Use 'targets' param, which allows specifying an array of target interfaces.",
        replaceWith = ReplaceWith("targets")
    )
    public val target: KClass<*> = Any::class,

    /**
     * Set target interfaces which proxy implementations
     * should be generated for. By default, proxies are generated for
     * all interfaces implemented by the class.
     *
     * You need to set this parameter only if you want to generate
     * proxies only for specific interfaces.
     */
    public val targets: Array<KClass<*>> = [],

    /**
     * Set a Hilt Component, where the target interface will
     * be installed in. Default Hilt Component is ActivityRetainedComponent.
     */
    public val installIn: KClass<*> = Any::class,

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
     *
     * For example:
     *
     * ```
     * interface MyEffects {
     *     fun showToast(message: String)
     *     fun cleanUp() = Unit // should not be abstract
     * }
     *
     * @HiltEffect(cleanUpMethodName = "cleanUp")
     * class MyEffectsImpl : MyEffects { ... }
     *
     * @HiltViewModel
     * class MyViewModel @Inject constructor(
     *     private val myEffects: MyEffects
     * ) {
     *
     *     override fun onCleared() {
     *         super.onCleared()
     *         myEffects.cleanUp()
     *     }
     *
     * }
     * ```
     *
     * In this particular case, calling `myEffects.cleanUp` in the
     * `ViewModel.onCleared` will cancel all non-processed commands
     * for displaying Toast-messages.
     */
    public val cleanUpMethodName: String = "",

    )
