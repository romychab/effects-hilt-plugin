package com.uandcode.example.koin.multimodule.app.di

import com.uandcode.example.koin.multimodule.features.details.CatDetailsViewModel
import com.uandcode.example.koin.multimodule.features.list.CatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {
    viewModelOf(::CatsViewModel)
    viewModelOf(::CatDetailsViewModel)
}
