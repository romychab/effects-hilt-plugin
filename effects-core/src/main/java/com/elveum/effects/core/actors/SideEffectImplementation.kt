package com.elveum.effects.core.actors

/**
 * For internal usage.
 *
 * Holds information about side-effect implementation on the activity side.
 * Lifecycle of the side-effect implementation is tied to the activity lifecycle.
 */
public class SideEffectImplementation(
    /**
     * Side-effect interface implementation instance
     */
    internal val instance: Any,

    /**
     * Key to the side-effect mediator instance.
     */
    internal val target: String
)