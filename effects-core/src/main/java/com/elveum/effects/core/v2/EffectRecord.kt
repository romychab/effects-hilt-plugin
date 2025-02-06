package com.elveum.effects.core.v2

import kotlin.reflect.KClass

public class EffectRecord(
    public val effectImplementationClass: KClass<*>,
    public val effectInterfaceClass: KClass<*>,
    private val controllerProvider: () -> EffectController<*>,
) {

    public val effectController: EffectController<*> by lazy {
        controllerProvider.invoke()
    }

}