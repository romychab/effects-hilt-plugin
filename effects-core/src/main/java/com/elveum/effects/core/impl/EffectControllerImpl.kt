package com.elveum.effects.core.impl

import com.elveum.effects.core.EffectController
import com.elveum.effects.core.ObservableResourceStore

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
