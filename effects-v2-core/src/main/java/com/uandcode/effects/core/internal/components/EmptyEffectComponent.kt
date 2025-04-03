package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.internal.DefaultProxyEffectFactory
import com.uandcode.effects.core.EffectInterfaces

internal val EmptyEffectComponent: EffectComponent = LazyEffectComponent {
    buildEmptyEffectComponent()
}

internal fun buildEmptyEffectComponent(): EffectComponent {
    return DefaultEffectComponent(
        interfaces = EffectInterfaces.ListOf(),
        proxyEffectFactory = DefaultProxyEffectFactory,
        parent = null,
    )
}
