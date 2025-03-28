package com.uandcode.effects.core

import com.uandcode.effects.core.internal.components.EmptyEffectComponent
import com.uandcode.effects.core.internal.components.GlobalEffectComponent

/**
 * This object contains 2 root effect components that can be used
 * for retrieving and managing lifecycle of auto-generated effect proxies.
 * 1) [RootEffectComponents.global] is a pre-defined singleton component which
 *    contains all proxies for all annotated effects.
 * 2) [RootEffectComponents.empty] is a root empty component that can be
 *    used for making your own hierarchy of effects with different lifecycle
 */
public object RootEffectComponents {

    private var _empty: EffectComponent = EmptyEffectComponent
    private var _global: EffectComponent = GlobalEffectComponent

    /**
     * This is an empty effect component that can be used to create your
     * own hierarchy of components.
     */
    public val empty: EffectComponent get() = _empty

    /**
     * Global singleton component contains references to all annotated effects.
     */
    public val global: EffectComponent get() = _global

    /**
     * Set a custom [EffectComponent] instance which will be used
     * by [empty] property as a default component.
     */
    public fun setEmpty(node: EffectComponent) {
        _empty = node
    }

    /**
     * Set a custom [EffectComponent] instance which will be used
     * by [global] property as a default component.
     */
    public fun setGlobal(node: EffectComponent) {
        _global = node
    }

    /**
     * Reset components in [empty] and [global] properties to
     * default ones.
     */
    public fun resetComponents() {
        _empty = EmptyEffectComponent
        _global = GlobalEffectComponent
    }

}
