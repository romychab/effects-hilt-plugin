package com.uandcode.effects.core

import com.uandcode.effects.core.internal.scopes.EmptyEffectScope
import com.uandcode.effects.core.internal.scopes.buildGlobalEffectScope

/**
 * This object contains 2 root effect scopes that can be used
 * for retrieving and managing lifecycle of auto-generated effect proxies.
 * 1) [RootEffectScopes.global] is a pre-defined singleton scope which
 *    contains all proxies for all annotated effects.
 * 2) [RootEffectScopes.empty] is a root empty scope that can be
 *    used for making your own hierarchy of effects with different lifecycle
 */
public object RootEffectScopes {

    private var _global: EffectScope = buildGlobalEffectScope()

    /**
     * This is an empty effect scope that can be used to create your
     * own hierarchy of scopes.
     */
    public val empty: EffectScope = EmptyEffectScope

    /**
     * Global singleton scope containing references to all annotated effects.
     */
    public val global: EffectScope get() = _global

    /**
     * Set a custom [EffectScope] instance which will be used
     * by [global] property as a default scope.
     */
    public fun setGlobal(scope: EffectScope) {
        _global = scope
    }

    /**
     * Reset scope in [global] property to
     * default one.
     */
    public fun resetGlobalScope() {
        _global = buildGlobalEffectScope()
    }

}
