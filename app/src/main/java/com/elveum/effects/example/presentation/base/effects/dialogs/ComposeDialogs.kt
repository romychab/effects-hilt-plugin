package com.elveum.effects.example.presentation.base.effects.dialogs

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.elveum.effects.annotations.HiltEffect
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@HiltEffect
@Stable
class ComposeDialogs(
    @ActivityContext private val context: Context,
) : Dialogs {

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

    override fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun Dialog() {
        this.dialogState?.apply {
            AlertDialog(
                onDismissRequest = ::reject,
                dismissButton = {
                    TextButton(::reject) {
                        Text(config.negativeButton)
                    }
                },
                confirmButton = {
                    TextButton(::confirm) {
                        Text(config.positiveButton)
                    }
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