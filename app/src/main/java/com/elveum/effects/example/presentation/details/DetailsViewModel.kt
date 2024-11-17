package com.elveum.effects.example.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.elveum.container.getOrNull
import com.elveum.effects.example.domain.CatsRepository
import com.elveum.effects.example.presentation.CatDetailsRoute
import com.elveum.effects.example.presentation.base.effects.navigation.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val catsRepository: CatsRepository,
    private val router: Router,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val catDetailsRoute = savedStateHandle.toRoute<CatDetailsRoute>()

    val catLiveData = catsRepository.getById(catDetailsRoute.catId).asLiveData()

    fun goBack() {
        router.goBack()
    }

    fun toggleLike() {
        viewModelScope.launch {
            catLiveData.value?.getOrNull()?.let { cat ->
                catsRepository.toggleLike(cat)
            }
        }
    }

}