package com.uandcode.effects.core.internal.scopes

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.factories.DefaultProxyEffectFactory
import com.uandcode.effects.core.ManagedInterfaces

internal fun buildGlobalEffectScope(): EffectScope {
    return LazyEffectScope {
        DefaultEffectScope(
            managedInterfaces = ManagedInterfaces.Everything,
            proxyEffectFactory = DefaultProxyEffectFactory(),
            parent = null,
        )
    }
}
