package com.uandcode.example.hilt.multimodule.effects.toasts

import android.content.Context
import android.widget.Toast
import com.uandcode.effects.hilt.annotations.HiltEffect
import com.uandcode.example.hilt.multimodule.effects.api.Toasts

@HiltEffect
public class AndroidToasts(
    private val context: Context,
) : Toasts {

    override fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
