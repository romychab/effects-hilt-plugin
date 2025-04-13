package com.uandcode.example.core.singlemodule.effects.dialogs

interface Dialogs {

    suspend fun showAlertDialog(config: AlertDialogConfig): Boolean

}
