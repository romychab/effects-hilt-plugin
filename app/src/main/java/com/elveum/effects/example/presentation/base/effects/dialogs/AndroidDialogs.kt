package com.elveum.effects.example.presentation.base.effects.dialogs

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.elveum.effects.annotations.SideEffect
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@SideEffect(target = Dialogs::class)
class AndroidDialogs(
    private val activity: FragmentActivity,
) : Dialogs {

    override suspend fun showAlertDialog(config: AlertDialogConfig): Boolean {
        return suspendCancellableCoroutine {  continuation ->
            val dialog = AlertDialog.Builder(activity)
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

    override fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}