package com.elveum.effects.example.presentation.base.effects.dialogs

interface Dialogs {

    suspend fun showAlertDialog(config: AlertDialogConfig): Boolean

    fun toast(message: String)

    fun cleanUp() = Unit

}
