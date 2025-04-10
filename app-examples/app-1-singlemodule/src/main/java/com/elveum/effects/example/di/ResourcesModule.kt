package com.elveum.effects.example.di

import com.elveum.effects.example.effects.resources.AndroidResources
import com.elveum.effects.example.effects.resources.Resources
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ResourcesModule {

    @Binds
    fun bindResources(
        impl: AndroidResources
    ): Resources

}