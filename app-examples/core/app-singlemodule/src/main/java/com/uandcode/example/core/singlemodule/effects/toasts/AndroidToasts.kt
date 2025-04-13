package com.uandcode.example.core.singlemodule.effects.toasts

import android.content.Context
import android.widget.Toast
import com.uandcode.effects.core.annotations.EffectClass

@EffectClass
class AndroidToasts(
    private val context: Context,
) : Toasts {

    override fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
