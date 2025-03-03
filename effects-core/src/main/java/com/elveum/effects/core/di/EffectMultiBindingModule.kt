package com.elveum.effects.core.di

import com.elveum.effects.core.EffectRecord
import com.elveum.effects.core.EffectStore
import com.elveum.effects.core.impl.EffectStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

@Module
@InstallIn(SingletonComponent::class)
internal object EffectMultiBindingModule {

    @Provides
    @ElementsIntoSet
    fun provideEffectRecords(): Set<@JvmSuppressWildcards EffectRecord> {
        return emptySet()
    }

    @Provides
    fun provideEffectStore(impl: EffectStoreImpl): EffectStore = impl

}
