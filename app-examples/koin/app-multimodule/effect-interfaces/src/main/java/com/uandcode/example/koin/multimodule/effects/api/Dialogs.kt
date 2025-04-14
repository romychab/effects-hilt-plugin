package com.uandcode.example.koin.multimodule.effects.api

public interface Dialogs {
    public suspend fun showAlertDialog(config: AlertDialogConfig): Boolean
}
