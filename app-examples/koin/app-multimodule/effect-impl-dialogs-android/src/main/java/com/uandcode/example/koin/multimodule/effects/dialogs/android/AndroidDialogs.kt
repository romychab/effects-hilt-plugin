package com.uandcode.example.koin.multimodule.effects.dialogs.android

import android.app.AlertDialog
import android.content.Context
import com.uandcode.effects.koin.annotations.KoinEffect
import com.uandcode.example.koin.multimodule.effects.api.AlertDialogConfig
import com.uandcode.example.koin.multimodule.effects.api.Dialogs
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@KoinEffect
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
