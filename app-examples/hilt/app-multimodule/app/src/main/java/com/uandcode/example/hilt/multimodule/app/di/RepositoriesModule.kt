package com.uandcode.example.hilt.multimodule.app.di

import com.uandcode.example.hilt.multimodule.features.details.domain.CatDetailsRepository
import com.uandcode.example.hilt.multimodule.features.list.domain.CatListRepository
import com.uandcode.example.hilt.multimodule.app.data.InMemoryCatsRepository
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
