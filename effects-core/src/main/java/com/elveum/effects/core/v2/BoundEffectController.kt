package com.elveum.effects.core.v2

/**
 * For internal usage!
 *
 * A wrapper on [EffectController] that manages a predefined [effectImplementation].
 *
 * @see EffectController
 */
public interface BoundEffectController<EffectImplementation> {

    /**
     * An effect implementation bounded to this controller.
     */
    public val effectImplementation: EffectImplementation

    /**
     * Whether the [effectImplementation] is attached to the target
     * interface right now or not.
     */
    public val isStarted: Boolean

    /**
     * Attach the [effectImplementation] to the target interface.
     */
    public fun start()

    /**
     * Detach the [effectImplementation] from the target interface.
     */
    public fun stop()

}
