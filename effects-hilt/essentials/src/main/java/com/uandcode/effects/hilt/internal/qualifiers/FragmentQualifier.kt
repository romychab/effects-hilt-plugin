package com.uandcode.effects.hilt.internal.qualifiers

import com.uandcode.effects.core.EffectScope

internal class FragmentQualifier(
    scope: EffectScope
) : AbstractInternalQualifier(scope) {
    override val priority: Int = 3
}
