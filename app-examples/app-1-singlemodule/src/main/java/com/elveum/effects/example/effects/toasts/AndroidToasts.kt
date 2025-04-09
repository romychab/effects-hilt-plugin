package com.elveum.effects.example.effects.toasts

import android.content.Context
import android.widget.Toast
import com.elveum.effects.annotations.HiltEffect

@HiltEffect
class AndroidToasts(
    private val context: Context,
) : Toasts {

    override fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
