package com.uandcode.example.koin.multimodule.features.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elveum.container.Container
import com.uandcode.example.koin.multimodule.effects.api.AlertDialogConfig
import com.uandcode.example.koin.multimodule.effects.api.Dialogs
import com.uandcode.example.koin.multimodule.effects.api.Resources
import com.uandcode.example.koin.multimodule.effects.api.Toasts
import com.uandcode.example.koin.multimodule.features.list.domain.Cat
import com.uandcode.example.koin.multimodule.features.list.domain.CatListRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatsViewModel(
    private val catListRepository: CatListRepository,
    private val router: CatsRouter,
    private val dialogs: Dialogs,
    private val toasts: Toasts,
    private val resources: Resources,
) : ViewModel() {

    val catsFlow = catListRepository.getCats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Container.Pending)

    fun processAction(action: CatsAction) = when(action) {
        is CatsAction.Delete -> delete(action.cat)
        is CatsAction.ShowDetails -> launchDetails(action.cat)
        is CatsAction.Toggle -> toggleLike(action.cat)
    }

    private fun toggleLike(cat: Cat) {
        viewModelScope.launch {
            catListRepository.toggleLike(cat)
        }
    }

    private fun delete(cat: Cat) {
        viewModelScope.launch {
            val confirmed = dialogs.showAlertDialog(buildDeleteDialog())
            if (confirmed) {
                catListRepository.deleteCat(cat)
                toasts.toast(resources.getString(R.string.cat_has_been_deleted))
            }
        }
    }

    private fun launchDetails(cat: Cat) {
        router.launchDetails(cat)
    }

    private fun buildDeleteDialog(): AlertDialogConfig = AlertDialogConfig(
        title = resources.getString(R.string.delete_cat_title),
        message = resources.getString(R.string.delete_cat_message),
        positiveButton = resources.getString(R.string.yes),
        negativeButton = resources.getString(R.string.no),
    )

}
