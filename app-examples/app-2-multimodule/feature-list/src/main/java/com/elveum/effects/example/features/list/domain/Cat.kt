package com.elveum.effects.example.features.list.domain

import androidx.compose.runtime.Immutable

@Immutable
interface Cat {
    val id: Long
    val name: String
    val details: String
    val image: String
    val isLiked: Boolean
}
