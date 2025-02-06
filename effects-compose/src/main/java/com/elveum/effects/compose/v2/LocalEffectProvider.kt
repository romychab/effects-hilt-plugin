package com.elveum.effects.compose.v2

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

public val LocalEffectProvider: ProvidableCompositionLocal<ComposeEffectProvider> =
    staticCompositionLocalOf { error("getEffect() can be called only within EffectProvider { ... }") }
