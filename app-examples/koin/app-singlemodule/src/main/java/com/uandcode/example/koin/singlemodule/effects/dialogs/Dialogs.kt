package com.uandcode.example.koin.singlemodule.effects.dialogs

interface Dialogs {

    suspend fun showAlertDialog(config: AlertDialogConfig): Boolean

}
