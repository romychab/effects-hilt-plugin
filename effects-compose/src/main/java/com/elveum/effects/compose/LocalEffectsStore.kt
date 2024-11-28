package com.elveum.effects.compose

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.elveum.effects.core.EffectsStore

/**
 * Local [EffectsStore] which is used by [getEffect] function to get instances
 * of side effects.
 */
public val LocalEffectsStore: ProvidableCompositionLocal<EffectsStore> =
    staticCompositionLocalOf {
        error("EffectsStore is not initialized, probably you've called getEffect outside of EffectsApp.")
    }
