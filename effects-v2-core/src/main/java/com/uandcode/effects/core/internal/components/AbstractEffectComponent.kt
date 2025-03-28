package com.uandcode.effects.core.internal.components

import com.uandcode.effects.core.EffectComponent
import kotlin.reflect.KClass

internal abstract class AbstractEffectComponent : EffectComponent {

    override fun createChild(vararg interfaces: KClass<*>): EffectComponent {
        return DefaultEffectComponent(interfaces.toSet(), this)
    }

}
