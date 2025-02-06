package com.elveum.effects.core.v2.di

import com.elveum.effects.core.v2.EffectRecord
import com.elveum.effects.core.v2.EffectStore
import com.elveum.effects.core.v2.impl.EffectStoreImpl
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
