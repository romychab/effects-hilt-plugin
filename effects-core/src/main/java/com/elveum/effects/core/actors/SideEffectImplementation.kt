package com.elveum.effects.core.actors

/**
 * For internal usage.
 *
 * Holds information about side-effect implementation on the activity side.
 * Lifecycle of the side-effect implementation is tied to the activity lifecycle.
 */
class SideEffectImplementation(
    /**
     * Side-effect interface implementation instance
     */
    val instance: Any,

    /**
     * Key to the side-effect mediator instance.
     */
    val target: String
)