package com.uandcode.example.core.multimodule.features.details.domain

import androidx.compose.runtime.Immutable

@Immutable
interface Cat {
    val name: String
    val details: String
    val image: String
    val isLiked: Boolean
}
