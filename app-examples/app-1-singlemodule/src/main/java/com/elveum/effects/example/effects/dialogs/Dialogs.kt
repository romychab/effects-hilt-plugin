package com.elveum.effects.example.effects.dialogs

interface Dialogs {

    suspend fun showAlertDialog(config: AlertDialogConfig): Boolean

}
