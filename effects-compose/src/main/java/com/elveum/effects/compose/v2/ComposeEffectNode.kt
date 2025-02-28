package com.elveum.effects.compose.v2

import kotlin.reflect.KClass

/**
 * For internal usage.
 */
public interface ComposeEffectNode {

    /**
     * Get an effect of type [T] by [clazz].
     */
    public fun <T : Any> getEffect(clazz: KClass<T>): T

    /**
     * Create a new node with the list of specified [effects].
     *
     * A new node inherits all effects from this node.
     */
    public fun attachNode(effects: List<Any>): ComposeEffectNode

    /**
     * Attach all effects managed by this node to their interfaces.
     */
    public fun start()

    /**
     * Detach all effects managed by this node from their interfaces.
     */
    public fun stop()

}
