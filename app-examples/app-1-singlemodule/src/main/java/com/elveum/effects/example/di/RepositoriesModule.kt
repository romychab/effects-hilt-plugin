package com.elveum.effects.example.di

import com.elveum.effects.example.data.InMemoryCatsRepository
import com.elveum.effects.example.domain.CatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoriesModule {

    @Binds
    fun bindCatsRepository(
        impl: InMemoryCatsRepository
    ): CatsRepository

}