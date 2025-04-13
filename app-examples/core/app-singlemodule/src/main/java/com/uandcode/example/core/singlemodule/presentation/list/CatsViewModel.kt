package com.uandcode.example.core.singlemodule.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elveum.container.Container
import com.uandcode.effects.core.RootEffectScopes
import com.uandcode.effects.core.getProxy
import com.uandcode.example.core.singlemodule.R
import com.uandcode.example.core.singlemodule.di.Singletons
import com.uandcode.example.core.singlemodule.domain.Cat
import com.uandcode.example.core.singlemodule.domain.CatsRepository
import com.uandcode.example.core.singlemodule.effects.dialogs.AlertDialogConfig
import com.uandcode.example.core.singlemodule.effects.dialogs.Dialogs
import com.uandcode.example.core.singlemodule.effects.navigation.Router
import com.uandcode.example.core.singlemodule.effects.resources.Resources
import com.uandcode.example.core.singlemodule.effects.toasts.Toasts
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CatsViewModel(
    private val catsRepository: CatsRepository = Singletons.catsRepository,
    private val router: Router = RootEffectScopes.global.getProxy(),
    private val dialogs: Dialogs = RootEffectScopes.global.getProxy(),
    private val toasts: Toasts = RootEffectScopes.global.getProxy(),
    private val resources: Resources = Singletons.resources,
) : ViewModel() {

    val catsFlow = catsRepository.getCats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Container.Pending)

    fun processAction(action: CatsAction) = when(action) {
        is CatsAction.Delete -> delete(action.cat)
        is CatsAction.ShowDetails -> launchDetails(action.cat)
        is CatsAction.Toggle -> toggleLike(action.cat)
    }

    private fun toggleLike(cat: Cat) {
        viewModelScope.launch {
            catsRepository.toggleLike(cat)
        }
    }

    private fun delete(cat: Cat) {
        viewModelScope.launch {
            val confirmed = dialogs.showAlertDialog(buildDeleteDialog())
            if (confirmed) {
                catsRepository.deleteCat(cat)
                toasts.toast(resources.getString(R.string.cat_has_been_deleted))
            }
        }
    }

    private fun launchDetails(cat: Cat) {
        router.launchDetails(cat.id)
    }

    private fun buildDeleteDialog(): AlertDialogConfig = AlertDialogConfig(
        title = resources.getString(R.string.delete_cat_title),
        message = resources.getString(R.string.delete_cat_message),
        positiveButton = resources.getString(R.string.yes),
        negativeButton = resources.getString(R.string.no),
    )

}