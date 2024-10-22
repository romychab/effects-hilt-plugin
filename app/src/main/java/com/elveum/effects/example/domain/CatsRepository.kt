package com.elveum.effects.example.domain

import com.elveum.container.ContainerFlow
import com.elveum.container.ListContainerFlow

interface CatsRepository {
    suspend fun deleteCat(cat: Cat)
    suspend fun toggleLike(cat: Cat)
    fun getById(id: Long): ContainerFlow<Cat>
    fun getCats(): ListContainerFlow<Cat>
}