package com.uandcode.example.koin.singlemodule.effects.dialogs

import android.app.AlertDialog
import android.content.Context
import com.uandcode.effects.koin.annotations.KoinEffect
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@KoinEffect
class AndroidDialogs(
    private val context: Context,
) : Dialogs {

    override suspend fun showAlertDialog(config: AlertDialogConfig): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val alertDialog = AlertDialog.Builder(context)
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
            alertDialog.show()
            continuation.invokeOnCancellation {
                alertDialog.dismiss()
            }
        }
    }

}