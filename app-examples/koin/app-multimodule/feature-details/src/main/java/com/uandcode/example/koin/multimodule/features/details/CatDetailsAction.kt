package com.uandcode.example.koin.multimodule.features.details

sealed class CatDetailsAction {
    data object ToggleLike : CatDetailsAction()
    data object GoBack : CatDetailsAction()
}
