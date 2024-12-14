package com.elveum.effects.core.actors

/**
 * For internal usage.
 *
 * Holds information about MVI-effect implementation on the activity side.
 * Lifecycle of the MVI-effect implementation is tied to the activity lifecycle.
 */
public class MviEffectImplementation(
    /**
     * MVI-effect interface implementation instance
     */
    internal val instance: Any,

    /**
     * Key to the MVI-effect mediator instance.
     */
    internal val target: String
)