package com.elveum.effects.example.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.elveum.container.Container
import com.elveum.effects.example.domain.Cat
import com.elveum.effects.example.domain.CatsRepository
import com.elveum.effects.example.presentation.base.BaseViewModel
import com.elveum.effects.example.presentation.base.effects.navigation.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val catsRepository: CatsRepository,
    private val router: Router,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    private val catId = DetailsFragmentArgs
        .fromSavedStateHandle(savedStateHandle)
        .catId

    val catLiveData = liveData<Container<Cat>>(Container.Pending)

    init {
        viewModelScope.launch {
            catLiveData.updateWith(Container.Success(catsRepository.getById(catId)))
        }
    }

    fun goBack() {
        router.goBack()
    }

}