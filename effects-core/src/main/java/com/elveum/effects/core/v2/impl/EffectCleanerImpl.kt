package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.EffectCleaner
import com.elveum.effects.core.v2.ObservableResourcesStore

public class EffectCleanerImpl<Effect>(
    private val observableResourcesStore: ObservableResourcesStore<Effect>
) : EffectCleaner<Effect> {

    override fun cleanUp() {
        observableResourcesStore.removeAllObservers()
    }

}