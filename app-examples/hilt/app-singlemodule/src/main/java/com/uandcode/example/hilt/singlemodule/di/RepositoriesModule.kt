package com.uandcode.example.hilt.singlemodule.di

import com.uandcode.example.hilt.singlemodule.data.InMemoryCatsRepository
import com.uandcode.example.hilt.singlemodule.domain.CatsRepository
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