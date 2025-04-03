package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.internal.DefaultProxyEffectFactory
import com.uandcode.effects.core.EffectInterfaces

internal val GlobalEffectComponent: EffectComponent = LazyEffectComponent {
    buildGlobalEffectComponent()
}

internal fun buildGlobalEffectComponent(): EffectComponent {
    return DefaultEffectComponent(
        interfaces = EffectInterfaces.Everything,
        proxyEffectFactory = DefaultProxyEffectFactory,
        parent = null,
    )
}
