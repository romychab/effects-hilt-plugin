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
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

@HiltEffect
@Stable
class ComposeDialogs(
    @ActivityContext private val context: Context,
) : Dialogs {

    private var dialogRecord by mutableStateOf<DialogRecord?>(null)

    override suspend fun showAlertDialog(config: AlertDialogConfig): Boolean {
        return suspendCancellableCoroutine { continuation ->
            this.dialogRecord = DialogRecord(config, continuation)
            continuation.invokeOnCancellation {
                this.dialogRecord = null
            }
        }
    }

    override fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun Dialog() {
        val finalDialogRecord = this.dialogRecord ?: return
        with(finalDialogRecord.config) {
            AlertDialog(
                onDismissRequest = {
                    finalDialogRecord.continuation.resume(false)
                    dialogRecord = null
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            finalDialogRecord.continuation.resume(true)
                            dialogRecord = null
                        }
                    ) {
                        Text(positiveButton)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            finalDialogRecord.continuation.resume(false)
                            dialogRecord = null
                        }
                    ) {
                        Text(negativeButton)
                    }
                },
                title = { Text(title) },
                text = { Text(message) },
            )
        }
    }

    private class DialogRecord(
        val config: AlertDialogConfig,
        val continuation: Continuation<Boolean>,
    )

}