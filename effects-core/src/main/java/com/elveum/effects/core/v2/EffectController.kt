package com.elveum.effects.core.v2

public interface EffectController<EffectImplementation> {
    public val effectImplementation: EffectImplementation?
    public fun start(effectImplementation: EffectImplementation)
    public fun stop()

    public val isStarted: Boolean get() = effectImplementation != null
}
