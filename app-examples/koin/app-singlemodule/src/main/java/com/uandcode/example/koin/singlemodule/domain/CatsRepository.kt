package com.uandcode.example.koin.singlemodule.domain

import com.elveum.container.Container
import kotlinx.coroutines.flow.Flow

interface CatsRepository {
    suspend fun deleteCat(cat: Cat)
    suspend fun toggleLike(cat: Cat)
    fun getById(id: Long): Flow<Container<Cat>>
    fun getCats(): Flow<Container<List<Cat>>>
}