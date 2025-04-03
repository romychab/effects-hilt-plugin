package com.uandcode.effects.core.runtime

import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.EffectInterfaces
import com.uandcode.effects.core.RootEffectComponents

public object RuntimeEffectComponents {

    /**
     * Create a new [EffectComponent] which can create proxy implementations
     * for all effect [interfaces] at runtime.
     *
     * It may be useful either in integration tests or if you don't want to
     * use compile time annotation processing for some reason.
     *
     * Usage example:
     *
     * ```
     * // replace a root effect component with a runtime one
     * RootEffectComponents.setGlobal(RuntimeEffectComponents.create())
     * ```
     */
    public fun create(
        interfaces: EffectInterfaces = EffectInterfaces.Everything
    ): EffectComponent {
        return RootEffectComponents.empty.createChild(
            interfaces = interfaces,
            proxyEffectFactory = RuntimeProxyEffectFactory()
        )
    }

    /**
     * Create a child [EffectComponent] which can create proxy implementations
     * for all [interfaces] at runtime.
     */
    public fun createChild(
        parent: EffectComponent,
        interfaces: EffectInterfaces = EffectInterfaces.Everything
    ): EffectComponent {
        return parent.createChild(
            interfaces = interfaces,
            proxyEffectFactory = RuntimeProxyEffectFactory()
        )
    }

}
