package com.elveum.effects.core.di

import com.elveum.effects.core.MviEffectsLifecycleController
import com.elveum.effects.core.MviEffectsStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
public interface MviEffectsEntryPoint {
    public fun getMviEffectsStore(): MviEffectsStore
    public fun getMviEffectsLifecycleController(): MviEffectsLifecycleController
}
