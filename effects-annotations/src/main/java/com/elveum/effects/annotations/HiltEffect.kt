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

    /**
     * If your class implements only 1 single interface, then
     * this interface is used by default as a target interface.
     *
     * You need to set this parameter only if your class implements
     * 2 or more interfaces.
     */
    public val target: KClass<*> = Any::class,

    /**
     * Set a Hilt Component, where the target interface will
     * be installed in. Default Hilt Component is ActivityRetainedComponent.
     */
    public val installIn: KClass<*> = Any::class,

)
