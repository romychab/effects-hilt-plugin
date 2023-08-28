package com.elveum.effects.example.domain

import com.elveum.container.ListContainerFlow

interface CatsRepository {
    suspend fun deleteCat(cat: Cat)
    suspend fun toggleLike(cat: Cat)
    suspend fun getById(id: Long): Cat
    fun getCats(): ListContainerFlow<Cat>
}
