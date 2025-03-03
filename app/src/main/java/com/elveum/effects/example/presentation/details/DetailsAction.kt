package com.elveum.effects.example.presentation.details

import com.elveum.effects.example.presentation.base.effects.actions.Action

sealed class DetailsAction : Action {
    data object GoBack : DetailsAction()
    data object ToggleLike : DetailsAction()
}