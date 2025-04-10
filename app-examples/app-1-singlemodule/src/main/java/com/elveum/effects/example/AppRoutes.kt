package com.elveum.effects.example

import kotlinx.serialization.Serializable

@Serializable
data object CatsRoute

@Serializable
data class CatDetailsRoute(
    val catId: Long,
)