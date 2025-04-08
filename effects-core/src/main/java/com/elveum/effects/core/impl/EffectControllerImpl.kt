package com.elveum.effects.core.impl

import com.elveum.effects.core.EffectController
import com.elveum.effects.core.ObservableResourceStore

public class EffectControllerImpl<EffectImplementation>(
    private val observableResourceStores: List<ObservableResourceStore<in EffectImplementation>>,
) : EffectController<EffectImplementation> {

    override var effectImplementation: EffectImplementation? = null

    override fun start(effectImplementation: EffectImplementation) {
        if (this.effectImplementation != null) return

        this.effectImplementation = effectImplementation
        observableResourceStores.forEach {
            it.attachResource(effectImplementation)
        }
    }

    override fun stop() {
        effectImplementation?.let { effectImplementation ->
            observableResourceStores.forEach { it.detachResource(effectImplementation) }
        }
        effectImplementation = null
    }

}
