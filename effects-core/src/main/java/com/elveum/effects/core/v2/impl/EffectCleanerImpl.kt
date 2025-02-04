package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.EffectCleaner
import com.elveum.effects.core.v2.ObservableResourceStore

public class EffectCleanerImpl<Effect>(
    private val observableResourceStore: ObservableResourceStore<Effect>
) : EffectCleaner<Effect> {

    override fun cleanUp() {
        observableResourceStore.removeAllObservers()
    }

}