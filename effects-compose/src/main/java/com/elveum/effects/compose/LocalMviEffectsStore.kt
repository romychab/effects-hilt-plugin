package com.elveum.effects.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.elveum.effects.core.MviEffectsStore

/**
 * Local [MviEffectsStore] which is used by [getMviEffect] function to get instances
 * of MVI-effects.
 */
public val LocalMviEffectsStore: ProvidableCompositionLocal<MviEffectsStore> =
    staticCompositionLocalOf {
        error("MviEffectsStore is not initialized, probably you've called getMviEffect outside of MviEffectsApp.")
    }
