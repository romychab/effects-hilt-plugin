package com.uandcode.example.core.multimodule.effects.toasts

import android.content.Context
import android.widget.Toast
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.example.core.multimodule.effects.api.Toasts

@EffectClass
public class AndroidToasts(
    private val context: Context,
) : Toasts {

    override fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
