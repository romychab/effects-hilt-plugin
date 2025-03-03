package com.elveum.effects.core.impl

import com.elveum.effects.core.BoundEffectController
import com.elveum.effects.core.EffectController

public class BoundEffectControllerImpl<EffectImplementation>(
    private val controller: EffectController<EffectImplementation>,
    override val effectImplementation: EffectImplementation,
) : BoundEffectController<EffectImplementation> {

    override val isStarted: Boolean by controller::isStarted

    override fun start() {
        controller.start(effectImplementation)
    }

    override fun stop() {
        controller.stop()
    }

}
