package com.uandcode.effects.core.internal.scopes

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.ManagedInterfaces
import com.uandcode.effects.core.factories.ProxyEffectFactory

internal fun buildEmptyEffectScope(
    proxyEffectFactory: ProxyEffectFactory
): EffectScope {
    return LazyEffectScope {
        DefaultEffectScope(
            managedInterfaces = ManagedInterfaces.ListOf(),
            proxyEffectFactory = proxyEffectFactory,
            parent = null,
        )
    }
}
