package com.uandcode.effects.core.internal.scopes

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.factories.DefaultProxyEffectFactory
import com.uandcode.effects.core.EffectInterfaces

internal val EmptyEffectScope: EffectScope = LazyEffectScope {
    buildEmptyEffectScope()
}

internal fun buildEmptyEffectScope(): EffectScope {
    return DefaultEffectScope(
        interfaces = EffectInterfaces.ListOf(),
        proxyEffectFactory = DefaultProxyEffectFactory.annotationBasedInstance,
        parent = null,
    )
}
