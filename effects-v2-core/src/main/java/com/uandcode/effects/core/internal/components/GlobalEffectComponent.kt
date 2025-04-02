package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.core.internal.ProxyEffectStoreProvider.getGeneratedProxyEffectStore

internal val GlobalEffectComponent: EffectComponent = LazyEffectComponent {
    buildGlobalEffectComponent()
}

internal fun buildGlobalEffectComponent(): EffectComponent {
    return DefaultEffectComponent(
        getGeneratedProxyEffectStore().allTargetInterfaces
    )
}
