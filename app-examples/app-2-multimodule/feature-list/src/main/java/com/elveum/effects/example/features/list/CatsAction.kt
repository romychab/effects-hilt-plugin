package com.elveum.effects.example.features.list

import com.elveum.effects.example.features.list.domain.Cat

sealed class CatsAction {
    data class Toggle(val cat: Cat) : CatsAction()
    data class Delete(val cat: Cat) : CatsAction()
    data class ShowDetails(val cat: Cat) : CatsAction()
}
