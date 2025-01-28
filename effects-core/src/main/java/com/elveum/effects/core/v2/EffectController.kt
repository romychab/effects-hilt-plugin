package com.elveum.effects.core.v2

public interface EffectController<EffectImplementation> {
    public fun start(effectImplementation: EffectImplementation)
    public fun stop()
}
