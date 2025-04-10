package com.elveum.effects.example.multimodule.app.di

import com.elveum.effects.example.features.details.domain.CatDetailsRepository
import com.elveum.effects.example.features.list.domain.CatListRepository
import com.elveum.effects.example.multimodule.app.data.InMemoryCatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoriesModule {

    @Binds
    fun bindCatListRepository(
        impl: InMemoryCatsRepository
    ): CatListRepository

    @Binds
    fun bindCatDetailsRepository(
        impl: InMemoryCatsRepository
    ): CatDetailsRepository

}
