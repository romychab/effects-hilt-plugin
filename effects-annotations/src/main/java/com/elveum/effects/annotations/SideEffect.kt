package com.elveum.effects.annotations

import kotlin.reflect.KClass

/**
 * Mark your class as an implementation of side-effect interface.
 * Requirements for the annotated implementation class:
 * - it should implement at least one interface
 * - if class implements more than one interface, the [target] argument
 *   must be specified
 * - class should not be abstract
 * - class can have non-empty constructor. This constructor can only have
 *   dependencies from the following Hilt components:
 *   - `SingletonComponent`
 *   - `ActivityRetainedComponent`
 *   - `ActivityComponent`.
 *   Also you can use Dagger Qualifiers on constructor arguments if needed.
 *
 * Requirements for the side-effect interface:
 * - all non-suspend methods should not return any type (except Flow)
 * - suspend methods can return any type
 * - methods can have generic type parameters
 * - target interface can't have generic type parameters
 *
 * Lifecycle of all side-effect interfaces which are injected to your view-models
 * (both for activities and fragments) are tied to the Hilt `ActivityRetainedComponent`.
 * So they survive upon configuration changes. If you use fragments, then their view-models
 * receive the same instance of the side-effect interface within the activity. So
 * actually the following rules work here:
 * - 1 activity = 1 side-effect interface instance for all fragments
 * - side-effect interface lifecycle = activity view-model lifecycle
 * - side-effect implementation lifecycle = activity lifecycle
 *
 * ```
 * time -->
 * +------------------------------------------------------------------+
 * | SideEffect interface (retains across configuration changes)      |
 * +------------------+-----------+-----+----------+------------------+
 * | Activity active  | onDestroy | ... | onCreate | Activity rotated |
 * +------------------+---------- +-----+----------+------------------+
 * | SideEffect impl. 1           |     | SideEffect impl. 2          |
 * +------------------+---------- +     +-----------------------------+
 * ```
 *
 * Lifecycle of all side-effect implementations are tied to the Hilt `ActivityComponent`.
 * So side-effect implementations are created every time when activity is recreated. They
 * do not survive upon configuration changes.
 *
 * Moreover, all suspend methods of the side-effect implementation are executed in
 * the custom coroutine scope which automatically cancels suspend methods being
 * executed when the activity is going to be stopped. Afterwards, when
 * the activity restarts, all cancelled non-finished suspend methods
 * are re-executed again.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class SideEffect(
    public val target: KClass<*> = Any::class,
)
