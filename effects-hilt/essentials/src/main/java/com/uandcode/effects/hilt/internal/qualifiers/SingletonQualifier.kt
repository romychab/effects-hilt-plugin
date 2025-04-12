package com.uandcode.effects.hilt.internal.qualifiers

import com.uandcode.effects.core.EffectScope

internal class SingletonQualifier(
    scope: EffectScope
) : AbstractInternalQualifier(scope) {
    override val priority: Int = 0
}
