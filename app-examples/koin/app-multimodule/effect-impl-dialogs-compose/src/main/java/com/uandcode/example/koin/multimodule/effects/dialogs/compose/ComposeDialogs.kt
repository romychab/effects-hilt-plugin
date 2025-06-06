package com.uandcode.example.koin.multimodule.effects.dialogs.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.uandcode.effects.koin.annotations.KoinEffect
import com.uandcode.example.koin.multimodule.effects.api.AlertDialogConfig
import com.uandcode.example.koin.multimodule.effects.api.Dialogs
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@KoinEffect
public class ComposeDialogs : Dialogs {

    private var dialogState by mutableStateOf<DialogState?>(null)

    override suspend fun showAlertDialog(config: AlertDialogConfig): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val onResponse: (Boolean) -> Unit = {
                continuation.resume(it)
                this.dialogState = null
            }
            this.dialogState = DialogState(config, onResponse)
            continuation.invokeOnCancellation {
                this.dialogState = null
            }
        }
    }

    @Composable
    public fun Dialog() {
        this.dialogState?.apply {
            AlertDialog(
                onDismissRequest = ::reject,
                dismissButton = {
                    TextButton(::reject) { Text(config.negativeButton) }
                },
                confirmButton = {
                    TextButton(::confirm) { Text(config.positiveButton) }
                },
                title = { Text(config.title) },
                text = { Text(config.message) },
            )
        }
    }

    private class DialogState(
        val config: AlertDialogConfig,
        val onResponse: (Boolean) -> Unit,
    ) {
        fun confirm() = onResponse(true)
        fun reject() = onResponse(false)
    }

}
