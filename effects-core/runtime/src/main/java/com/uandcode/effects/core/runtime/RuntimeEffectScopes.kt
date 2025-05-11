package com.uandcode.effects.core.runtime

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.RootEffectScopes

public object RuntimeEffectScopes {

    public fun createEmpty(): EffectScope {
        return RootEffectScopes.empty(RuntimeProxyEffectFactory())
    }

    /**
     * Create a new [EffectScope] which can create proxy implementations
     * for all effect [managedInterfaces] at runtime.
     *
     * It may be useful either in integration tests or if you don't want to
     * use compile time annotation processing for some reason.
     *
     * Usage example:
     *
     * ```
     * // replace a root effect scope with a runtime one
     * RootEffectScopes.setGlobal(RuntimeEffectScopes.createGlobal())
     * ```
     */
    public fun createGlobal(
        managedInterfaces: ManagedInterfaces = ManagedInterfaces.Everything
    ): EffectScope {
        return createEmpty().createChild(managedInterfaces)
    }

}
