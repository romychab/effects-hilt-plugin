package com.elveum.effects.example.toasts

import android.content.Context
import android.widget.Toast
import com.elveum.effects.annotations.HiltEffect
import com.elveum.effects.example.effects.api.Toasts
import dagger.hilt.components.SingletonComponent

@HiltEffect(installIn = SingletonComponent::class)
public class AndroidToasts(
    private val context: Context,
) : Toasts {

    override fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
