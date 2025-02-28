package com.elveum.effects.core.v2

/**
 * This interface can be used for cleaning/cancelling
 * any non-finished calls executed on a [Effect] interface.
 *
 * Usually you don't need to use this interface.
 */
public interface EffectCleaner<Effect> {

    /**
     * Cancel all calls being executed on a [Effect] interface. Also all
     * calls that hasn't been executed yet, but located in a command queue,
     * will be removed from the queue.
     */
    public fun cleanUp()

}
