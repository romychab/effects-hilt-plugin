package com.uandcode.example.koin.singlemodule.di

import com.uandcode.example.koin.singlemodule.presentation.details.CatDetailsViewModel
import com.uandcode.example.koin.singlemodule.presentation.list.CatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {
    viewModelOf(::CatsViewModel)
    viewModelOf(::CatDetailsViewModel)
}
