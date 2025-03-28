package com.uandcode.effects.core.internal

import com.uandcode.effects.core.BoundEffectController
import com.uandcode.effects.core.EffectController

internal class BoundEffectControllerImpl<EffectImplementation>(
    private val controller: EffectController<EffectImplementation>,
    effectImplementationProvider: () -> EffectImplementation,
) : BoundEffectController<EffectImplementation> {

    override val effectImplementation: EffectImplementation by lazy {
        effectImplementationProvider()
    }

    override val isStarted: Boolean by controller::isStarted

    override fun start() {
        controller.start(effectImplementation)
    }

    override fun stop() {
        controller.stop()
    }

}
