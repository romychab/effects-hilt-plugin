package com.uandcode.example.koin.multimodule.features.list

import com.uandcode.example.koin.multimodule.features.list.domain.Cat

sealed class CatsAction {
    data class Toggle(val cat: Cat) : CatsAction()
    data class Delete(val cat: Cat) : CatsAction()
    data class ShowDetails(val cat: Cat) : CatsAction()
}
