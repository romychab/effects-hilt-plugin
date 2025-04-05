package com.uandcode.effects.core.compiler.api.data

import com.uandcode.effects.core.compiler.Const

/**
 * Represents the clean-up method name for an effect. Default name
 * is returned automatically if the origin name is blank.
 */
public class EffectCleanUpMethodName(
    public val originCleanUpMethodName: String
) {
    public val isDefaultCleanUpMethodName: Boolean = originCleanUpMethodName.isBlank()
    public val simpleText: String = if (isDefaultCleanUpMethodName)
        Const.DefaultCleanUpMethodName
    else
        originCleanUpMethodName
}
