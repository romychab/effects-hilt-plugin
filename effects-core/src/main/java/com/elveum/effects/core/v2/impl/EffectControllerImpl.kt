package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.EffectController
import com.elveum.effects.core.v2.ObservableResourceStore


public class EffectControllerImpl<EffectImplementation>(
    private val observableResourceStore: ObservableResourceStore<EffectImplementation>
) : EffectController<EffectImplementation> {

    private var currentEffectImplementation: EffectImplementation? = null

    override fun start(effectImplementation: EffectImplementation) {
        if (currentEffectImplementation != null) return

        currentEffectImplementation = effectImplementation
        observableResourceStore.attachResource(effectImplementation)
    }

    override fun stop() {
        currentEffectImplementation?.let(observableResourceStore::detachResource)
        currentEffectImplementation = null
    }

}