package com.uandcode.example.hilt.multimodule.effects.dialogs.android

import android.app.AlertDialog
import android.content.Context
import com.uandcode.example.hilt.multimodule.effects.api.AlertDialogConfig
import com.uandcode.example.hilt.multimodule.effects.api.Dialogs
import com.uandcode.effects.hilt.annotations.HiltEffect
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@HiltEffect
public class AndroidDialogs(
    private val context: Context,
) : Dialogs {

    override suspend fun showAlertDialog(config: AlertDialogConfig): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val dialog = AlertDialog.Builder(context)
                .setTitle(config.title)
                .setMessage(config.message)
                .setPositiveButton(config.positiveButton) { _, _ ->
                    continuation.resume(true)
                }
                .setNegativeButton(config.negativeButton) { _, _ ->
                    continuation.resume(false)
                }
                .setOnCancelListener {
                    continuation.resume(false)
                }
                .create()
            dialog.show()
            continuation.invokeOnCancellation {
                dialog.dismiss()
            }
        }
    }

}
