package com.elveum.effects.example.presentation.list

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.elveum.effects.example.R
import com.elveum.effects.example.domain.Cat
import com.elveum.effects.example.domain.CatsRepository
import com.elveum.effects.example.presentation.base.BaseViewModel
import com.elveum.effects.example.presentation.base.effects.dialogs.AlertDialogConfig
import com.elveum.effects.example.presentation.base.effects.dialogs.Dialogs
import com.elveum.effects.example.presentation.base.effects.navigation.Router
import com.elveum.effects.example.presentation.base.resources.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CatsViewModel @Inject constructor(
    private val catsRepository: CatsRepository,
    private val dialogs: Dialogs,
    private val router: Router,
    private val resources: Resources,
) : BaseViewModel() {

    val catsLiveData = catsRepository.getCats().asLiveData()

    fun toggleLike(cat: Cat) {
        viewModelScope.launch {
            catsRepository.toggleLike(cat)
        }
    }

    fun delete(cat: Cat) {
        viewModelScope.launch {
            val confirmed = dialogs.showAlertDialog(buildDeleteDialog())
            if (confirmed) {
                catsRepository.deleteCat(cat)
                dialogs.toast(resources.getString(R.string.cat_has_been_deleted))
            }
        }
    }

    fun launchDetails(cat: Cat) {
        viewModelScope.launch {
            router.launchDetails(cat)
        }
    }

    private fun buildDeleteDialog(): AlertDialogConfig = AlertDialogConfig(
        title = resources.getString(R.string.delete_cat_title),
        message = resources.getString(R.string.delete_cat_message),
        positiveButton = resources.getString(R.string.yes),
        negativeButton = resources.getString(R.string.no),
    )

}