package com.uandcode.example.koin.singlemodule.di

import com.uandcode.example.koin.singlemodule.data.InMemoryCatsRepository
import com.uandcode.example.koin.singlemodule.domain.CatsRepository
import com.uandcode.example.koin.singlemodule.effects.resources.AndroidResources
import com.uandcode.example.koin.singlemodule.effects.resources.Resources
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val singletonsModule = module {
    singleOf(::InMemoryCatsRepository) bind CatsRepository::class
    singleOf(::AndroidResources) bind Resources::class
}
