package com.uandcode.example.core.multimodule.app.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CatsRoute

@Serializable
data class CatDetailsRoute(
    val catId: Long,
)
