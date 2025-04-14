package com.uandcode.example.koin.multimodule.features.details.domain

import com.elveum.container.Container
import kotlinx.coroutines.flow.Flow

interface CatDetailsRepository {
    fun getById(id: Long): Flow<Container<Cat>>
    suspend fun toggleLike(cat: Cat)
}
