package com.uandcode.example.hilt.singlemodule.effects.dialogs

interface Dialogs {

    suspend fun showAlertDialog(config: AlertDialogConfig): Boolean

}
