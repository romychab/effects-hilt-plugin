package com.elveum.effects.core

import kotlin.reflect.KClass

/**
 * For internal usage!
 *
 * This class allows getting [EffectController] instances without direct
 * usage of @Inject annotation.
 */
public class EffectRecord(
    public val effectImplementationClass: KClass<*>,
    public val effectInterfaceClasses: List<KClass<*>>,
    private val controllerProvider: () -> EffectController<*>,
) {

    public val effectController: EffectController<*> by lazy {
        controllerProvider.invoke()
    }

}
