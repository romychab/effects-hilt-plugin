package com.elveum.effects.example.presentation.list

import com.elveum.effects.example.domain.Cat
import com.elveum.effects.example.presentation.base.Action

sealed class CatsAction : Action {
    data class Toggle(val cat: Cat) : CatsAction()
    data class Delete(val cat: Cat) : CatsAction()
    data class ShowDetails(val cat: Cat) : CatsAction()
}
