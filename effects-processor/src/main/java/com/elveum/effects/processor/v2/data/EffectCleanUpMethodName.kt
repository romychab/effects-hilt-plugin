package com.elveum.effects.processor.v2.data

class EffectCleanUpMethodName(
    val originCleanUpMethodName: String
) {
    val isDefaultCleanUpMethodName: Boolean = originCleanUpMethodName.isBlank()
    val simpleText: String = if (isDefaultCleanUpMethodName)
        Const.CleanUpMethodNameArgumentDefaultValue
    else
        originCleanUpMethodName
}
