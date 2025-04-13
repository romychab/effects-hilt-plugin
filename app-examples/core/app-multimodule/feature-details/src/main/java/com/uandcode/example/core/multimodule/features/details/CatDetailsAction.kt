package com.uandcode.example.core.multimodule.features.details

sealed class CatDetailsAction {
    data object ToggleLike : CatDetailsAction()
    data object GoBack : CatDetailsAction()
}
