package com.elveum.effects.example.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elveum.container.getOrNull
import com.elveum.effects.example.domain.CatsRepository
import com.elveum.effects.example.presentation.CatDetailsRoute
import com.elveum.effects.example.presentation.base.effects.actions.UiActions
import com.elveum.effects.example.presentation.base.effects.actions.handleActions
import com.elveum.effects.example.presentation.base.effects.navigation.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val catsRepository: CatsRepository,
    private val router: Router,
    uiActions: UiActions,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val catDetailsRoute = savedStateHandle.toRoute<CatDetailsRoute>()

    val catLiveData = catsRepository.getById(catDetailsRoute.catId).asLiveData()

    init {
        uiActions.handleActions<DetailsAction>(viewModelScope) { action ->
            when (action) {
                DetailsAction.GoBack -> goBack()
                DetailsAction.ToggleLike -> toggleLike()
            }
        }
    }

    private fun toggleLike() {
        viewModelScope.launch {
            catLiveData.value?.getOrNull()?.let { cat ->
                catsRepository.toggleLike(cat)
            }
        }
    }

    private fun goBack() {
        router.goBack()
    }

}