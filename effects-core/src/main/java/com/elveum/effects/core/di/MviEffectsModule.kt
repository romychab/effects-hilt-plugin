package com.elveum.effects.core.di

import com.elveum.effects.core.MviEffectsLifecycleController
import com.elveum.effects.core.MviEffectsLifecycleControllerImpl
import com.elveum.effects.core.MviEffectsStore
import com.elveum.effects.core.MviEffectsStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
internal interface MviEffectsModule {
    @Binds
    fun bindEffectsStore(impl: MviEffectsStoreImpl): MviEffectsStore

    @Binds
    fun bindEffectsLifecycleController(impl: MviEffectsLifecycleControllerImpl): MviEffectsLifecycleController
}
