package com.uandcode.effects.core.internal.scopes

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.factories.DefaultProxyEffectFactory
import com.uandcode.effects.core.ManagedInterfaces

internal val EmptyEffectScope: EffectScope = LazyEffectScope {
    buildEmptyEffectScope()
}

internal fun buildEmptyEffectScope(): EffectScope {
    return DefaultEffectScope(
        managedInterfaces = ManagedInterfaces.ListOf(),
        proxyEffectFactory = DefaultProxyEffectFactory(),
        parent = null,
    )
}
