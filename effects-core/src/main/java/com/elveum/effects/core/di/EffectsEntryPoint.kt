package com.elveum.effects.core.di

import com.elveum.effects.core.EffectsLifecycleController
import com.elveum.effects.core.EffectsStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
public interface EffectsEntryPoint {
    public fun getEffectsStore(): EffectsStore
    public fun getEffectsLifecycleController(): EffectsLifecycleController
}
