package com.uandcode.example.koin.singlemodule.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elveum.container.Container
import com.elveum.container.getOrNull
import com.uandcode.example.koin.singlemodule.CatDetailsRoute
import com.uandcode.example.koin.singlemodule.domain.Cat
import com.uandcode.example.koin.singlemodule.domain.CatsRepository
import com.uandcode.example.koin.singlemodule.effects.navigation.Router
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val catsRepository: CatsRepository,
    private val router: Router,
) : ViewModel() {

    private val catDetailsRoute = savedStateHandle.toRoute<CatDetailsRoute>()

    val catFlow: StateFlow<Container<Cat>> = catsRepository.getById(catDetailsRoute.catId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Container.Pending)

    fun processAction(action: CatDetailsAction) = when (action) {
        CatDetailsAction.GoBack -> goBack()
        CatDetailsAction.ToggleLike -> toggleLike()
    }

    private fun toggleLike() {
        viewModelScope.launch {
            catFlow.value.getOrNull()?.let { cat ->
                catsRepository.toggleLike(cat)
            }
        }
    }

    private fun goBack() {
        router.goBack()
    }

}
