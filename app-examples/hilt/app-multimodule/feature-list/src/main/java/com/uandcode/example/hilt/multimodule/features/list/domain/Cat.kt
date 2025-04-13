package com.uandcode.example.hilt.multimodule.features.list.domain

import androidx.compose.runtime.Immutable

@Immutable
interface Cat {
    val id: Long
    val name: String
    val details: String
    val image: String
    val isLiked: Boolean
}
