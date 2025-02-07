package com.elveum.effects.core.v2.impl

import com.elveum.effects.core.v2.BoundEffectController
import com.elveum.effects.core.v2.EffectController

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
