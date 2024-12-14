package com.elveum.effects.core.actors

/**
 * For internal usage.
 *
 * Base interface for all MVI-effect mediators.
 * MVI-effect mediator acts on the activity view-model side.
 * The lifecycle of mediator is the same as lifecycle of activity view-models.
 */
public interface MviEffectMediator<T> {
    public var target: T?
}