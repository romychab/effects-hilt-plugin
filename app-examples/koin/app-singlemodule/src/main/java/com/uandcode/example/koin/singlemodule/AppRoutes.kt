package com.uandcode.example.koin.singlemodule

import kotlinx.serialization.Serializable

@Serializable
data object CatsRoute

@Serializable
data class CatDetailsRoute(
    val catId: Long,
)