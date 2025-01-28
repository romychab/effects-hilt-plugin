package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.EffectController
import com.elveum.effects.core.v2.ObservableResourcesStore


public class EffectControllerImpl<EffectImplementation>(
    private val observableResourcesStore: ObservableResourcesStore<EffectImplementation>
) : EffectController<EffectImplementation> {

    private var currentEffectImplementation: EffectImplementation? = null

    override fun start(effectImplementation: EffectImplementation) {
        if (currentEffectImplementation != null) return

        currentEffectImplementation = effectImplementation
        observableResourcesStore.attachResource(effectImplementation)
    }

    override fun stop() {
        currentEffectImplementation?.let(observableResourcesStore::detachResource)
        currentEffectImplementation = null
    }

}