package com.elveum.effects.core.actors

/**
 * For internal usage.
 *
 * Holds information about mediator instance.
 */
public class SidePair(
    internal val interfaceName: String,
    internal val mediator: SideEffectMediator<Any>
)