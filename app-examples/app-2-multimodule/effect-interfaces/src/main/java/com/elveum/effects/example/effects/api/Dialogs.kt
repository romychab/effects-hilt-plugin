package com.elveum.effects.example.effects.api

public interface Dialogs {
    public suspend fun showAlertDialog(config: AlertDialogConfig): Boolean
}
