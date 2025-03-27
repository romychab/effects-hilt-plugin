package com.uandcode.effects.core

/**
 * This interface allows you manual managing a lifecycle of an effect implementation
 * of type [EffectImplementation].
 *
 * You can use this interface for attaching any effect implementation
 * to the target interface.
 */
public interface EffectController<EffectImplementation> {

    /**
     * The current effect implementation attached to the effect interface if available.
     */
    public val effectImplementation: EffectImplementation?

    /**
     * Attach the specified [effectImplementation] to the target interface,
     * so any method executed on the interface will be delegated to the
     * [effectImplementation] instance.
     *
     * If this controller has already attached effect implementation, nothing happens.
     */
    public fun start(effectImplementation: EffectImplementation)

    /**
     * Detach the previously attached [effectImplementation] from the target
     * interface.
     */
    public fun stop()

    /**
     * Whether an effect implementation is attached or not at this moment.
     */
    public val isStarted: Boolean get() = effectImplementation != null

}
