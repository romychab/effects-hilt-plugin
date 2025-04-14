package com.uandcode.example.koin.singlemodule.presentation.details

sealed class CatDetailsAction {
    data object ToggleLike : CatDetailsAction()
    data object GoBack : CatDetailsAction()
}
