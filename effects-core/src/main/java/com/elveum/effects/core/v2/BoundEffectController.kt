package com.elveum.effects.core.v2

public interface BoundEffectController<EffectImplementation> {
    public val effectImplementation: EffectImplementation
    public fun start()
    public fun stop()
}
