package com.uandcode.effects.core.internal

import com.uandcode.effects.core.EffectController

internal class EffectControllerImpl<EffectImplementation>(
    private val stores: List<ObservableResourceStore<in EffectImplementation>>
) : EffectController<EffectImplementation> {

    override var effectImplementation: EffectImplementation? = null

    override fun start(effectImplementation: EffectImplementation) {
        if (this.effectImplementation != null) return

        this.effectImplementation = effectImplementation
        stores.forEach { it.attachResource(effectImplementation) }
    }

    override fun stop() {
        effectImplementation?.let {
            stores.forEach { store -> store.detachResource(it) }
        }
        effectImplementation = null
    }

}
