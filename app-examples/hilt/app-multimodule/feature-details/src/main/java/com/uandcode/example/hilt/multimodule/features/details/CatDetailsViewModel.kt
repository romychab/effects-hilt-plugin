package com.uandcode.example.hilt.multimodule.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elveum.container.Container
import com.elveum.container.getOrNull
import com.uandcode.example.hilt.multimodule.features.details.domain.Cat
import com.uandcode.example.hilt.multimodule.features.details.domain.CatDetailsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = CatDetailsViewModel.Factory::class)
class CatDetailsViewModel @AssistedInject constructor(
    @Assisted catId: Long,
    private val catDetailsRepository: CatDetailsRepository,
    private val router: CatDetailsRouter,
) : ViewModel() {

    val catFlow: StateFlow<Container<Cat>> = catDetailsRepository.getById(catId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Container.Pending)

    fun processAction(action: CatDetailsAction) = when (action) {
        CatDetailsAction.GoBack -> goBack()
        CatDetailsAction.ToggleLike -> toggleLike()
    }

    private fun toggleLike() {
        viewModelScope.launch {
            catFlow.value.getOrNull()?.let { cat ->
                catDetailsRepository.toggleLike(cat)
            }
        }
    }

    private fun goBack() {
        router.goBack()
    }

    @AssistedFactory
    interface Factory {
        fun create(catId: Long): CatDetailsViewModel
    }

}
