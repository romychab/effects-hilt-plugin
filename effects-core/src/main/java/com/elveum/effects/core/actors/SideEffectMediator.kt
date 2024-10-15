package com.elveum.effects.core.actors

/**
 * For internal usage.
 *
 * Base interface for all side-effect mediators.
 * Side effect mediator acts on the activity view-model side.
 * The lifecycle of mediator is the same as lifecycle of activity view-models.
 */
public interface SideEffectMediator<T> {
    public var target: T?
}