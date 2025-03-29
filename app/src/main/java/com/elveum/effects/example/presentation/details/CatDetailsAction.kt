package com.elveum.effects.example.presentation.details

import com.elveum.effects.example.presentation.base.Action

sealed class CatDetailsAction : Action {
    data object ToggleLike : CatDetailsAction()
    data object GoBack : CatDetailsAction()
}
