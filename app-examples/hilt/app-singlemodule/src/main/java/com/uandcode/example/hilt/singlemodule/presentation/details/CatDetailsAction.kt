package com.uandcode.example.hilt.singlemodule.presentation.details

sealed class CatDetailsAction {
    data object ToggleLike : CatDetailsAction()
    data object GoBack : CatDetailsAction()
}
