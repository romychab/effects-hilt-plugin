package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.EffectComponent
import com.uandcode.effects.stub.GeneratedProxyEffectStore

internal val GlobalEffectComponent: EffectComponent = LazyEffectComponent {
    buildGlobalEffectComponent()
}

internal fun buildGlobalEffectComponent(): EffectComponent {
    return DefaultEffectComponent(GeneratedProxyEffectStore.allTargetInterfaces)
}
