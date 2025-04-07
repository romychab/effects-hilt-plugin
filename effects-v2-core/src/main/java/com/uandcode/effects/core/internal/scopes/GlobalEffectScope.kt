package com.uandcode.effects.core.internal.scopes

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.core.factories.DefaultProxyEffectFactory
import com.uandcode.effects.core.EffectInterfaces

internal fun buildGlobalEffectScope(): EffectScope {
    return LazyEffectScope {
        DefaultEffectScope(
            interfaces = EffectInterfaces.Everything,
            proxyEffectFactory = DefaultProxyEffectFactory.annotationBasedInstance,
            parent = null,
        )
    }
}
