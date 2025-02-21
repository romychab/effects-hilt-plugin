package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.EffectController
import com.elveum.effects.core.v2.ObservableResourceStore

public class EffectControllerImpl<EffectImplementation>(
    private val observableResourceStore: ObservableResourceStore<in EffectImplementation>
) : EffectController<EffectImplementation> {

    override var effectImplementation: EffectImplementation? = null

    override fun start(effectImplementation: EffectImplementation) {
        if (this.effectImplementation != null) return

        this.effectImplementation = effectImplementation
        observableResourceStore.attachResource(effectImplementation)
    }

    override fun stop() {
        effectImplementation?.let(observableResourceStore::detachResource)
        effectImplementation = null
    }

}
