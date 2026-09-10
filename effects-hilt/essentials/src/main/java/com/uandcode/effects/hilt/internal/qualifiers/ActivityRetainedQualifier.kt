package com.uandcode.effects.hilt.internal.qualifiers

import com.uandcode.effects.core.EffectScope
import com.uandcode.effects.hilt.InternalEffectsHiltApi

@InternalEffectsHiltApi
public class ActivityRetainedQualifier(
    scope: EffectScope
) : AbstractInternalQualifier(scope) {
    override val priority: Int = 1
}
