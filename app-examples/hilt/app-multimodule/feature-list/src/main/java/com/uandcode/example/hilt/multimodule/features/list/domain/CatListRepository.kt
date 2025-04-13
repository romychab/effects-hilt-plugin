package com.uandcode.example.hilt.multimodule.features.list.domain

import com.elveum.container.Container
import kotlinx.coroutines.flow.Flow

interface CatListRepository {
    fun getCats(): Flow<Container<List<Cat>>>
    suspend fun toggleLike(cat: Cat)
    suspend fun deleteCat(cat: Cat)
}
