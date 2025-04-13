package com.uandcode.example.core.multimodule.effects.api

public interface Dialogs {
    public suspend fun showAlertDialog(config: AlertDialogConfig): Boolean
}
