package com.uandcode.example.hilt.multimodule.app.di

import com.uandcode.example.hilt.multimodule.effects.api.Resources
import com.uandcode.example.hilt.multimodule.app.AndroidResources
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
