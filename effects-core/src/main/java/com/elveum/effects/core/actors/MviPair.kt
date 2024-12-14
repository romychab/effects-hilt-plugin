package com.elveum.effects.core.actors

/**
 * For internal usage.
 *
 * Holds information about mediator instance.
 */
public class MviPair(
    internal val interfaceName: String,
    internal val mediator: MviEffectMediator<Any>
)