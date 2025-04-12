package com.uandcode.effects.hilt.internal.qualifiers

import com.uandcode.effects.core.EffectScope

internal class ActivityRetainedQualifier(
    scope: EffectScope
) : AbstractInternalQualifier(scope) {
    override val priority: Int = 1
}
