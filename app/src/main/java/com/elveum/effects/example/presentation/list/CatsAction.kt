package com.elveum.effects.example.presentation.list

import com.elveum.effects.example.domain.Cat
import com.elveum.effects.example.presentation.base.effects.actions.Action

sealed class CatsAction : Action {
    data class LaunchDetails(val cat: Cat) : CatsAction()
    data class ToggleLike(val cat: Cat) : CatsAction()
    data class Delete(val cat: Cat) : CatsAction()
}