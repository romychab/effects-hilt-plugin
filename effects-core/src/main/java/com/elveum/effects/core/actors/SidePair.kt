package com.elveum.effects.core.actors

/**
 * For internal usage.
 *
 * Holds information about mediator instance.
 */
class SidePair(
    val interfaceName: String,
    val mediator: SideEffectMediator<Any>
)