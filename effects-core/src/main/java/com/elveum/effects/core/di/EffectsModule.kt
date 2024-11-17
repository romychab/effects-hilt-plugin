package com.elveum.effects.core.di

import com.elveum.effects.core.EffectsLifecycleController
import com.elveum.effects.core.EffectsLifecycleControllerImpl
import com.elveum.effects.core.EffectsStore
import com.elveum.effects.core.EffectsStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
internal interface EffectsModule {
    @Binds
    fun bindEffectsStore(impl: EffectsStoreImpl): EffectsStore

    @Binds
    fun bindEffectsLifecycleController(impl: EffectsLifecycleControllerImpl): EffectsLifecycleController
}
