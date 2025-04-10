package com.elveum.effects.example.presentation.details

sealed class CatDetailsAction {
    data object ToggleLike : CatDetailsAction()
    data object GoBack : CatDetailsAction()
}
