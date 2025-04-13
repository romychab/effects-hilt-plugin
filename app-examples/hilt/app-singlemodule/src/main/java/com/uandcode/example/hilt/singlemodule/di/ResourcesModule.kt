package com.uandcode.example.hilt.singlemodule.di

import com.uandcode.example.hilt.singlemodule.effects.resources.AndroidResources
import com.uandcode.example.hilt.singlemodule.effects.resources.Resources
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