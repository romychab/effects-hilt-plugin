package com.uandcode.example.hilt.multimodule.effects.api

public interface Dialogs {
    public suspend fun showAlertDialog(config: AlertDialogConfig): Boolean
}
