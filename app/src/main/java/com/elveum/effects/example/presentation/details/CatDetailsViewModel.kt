package com.elveum.effects.example.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elveum.container.Container
import com.elveum.container.getOrNull
import com.elveum.effects.example.domain.Cat
import com.elveum.effects.example.domain.CatsRepository
import com.elveum.effects.example.presentation.CatDetailsRoute
import com.elveum.effects.example.presentation.base.effects.navigation.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatDetailsViewModel @Inject constructor(
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
