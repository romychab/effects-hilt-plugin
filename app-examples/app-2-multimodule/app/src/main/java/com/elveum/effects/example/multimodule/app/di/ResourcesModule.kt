package com.elveum.effects.example.multimodule.app.di

import com.elveum.effects.example.effects.api.Resources
import com.elveum.effects.example.multimodule.app.AndroidResources
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
