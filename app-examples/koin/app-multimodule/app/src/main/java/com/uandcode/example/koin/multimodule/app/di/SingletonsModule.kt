package com.uandcode.example.koin.multimodule.app.di

import com.uandcode.example.koin.multimodule.app.AndroidResources
import com.uandcode.example.koin.multimodule.app.data.InMemoryCatsRepository
import com.uandcode.example.koin.multimodule.effects.api.Resources
import com.uandcode.example.koin.multimodule.features.details.domain.CatDetailsRepository
import com.uandcode.example.koin.multimodule.features.list.domain.CatListRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val singletonsModule = module {
    singleOf(::InMemoryCatsRepository) binds arrayOf(
        CatListRepository::class,
        CatDetailsRepository::class,
    )
    singleOf(::AndroidResources) bind Resources::class
}
