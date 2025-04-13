package com.uandcode.example.core.multimodule.app

import android.content.Context
import androidx.annotation.StringRes
import com.uandcode.example.core.multimodule.effects.api.Resources

class AndroidResources(
    private val context: Context
) : Resources {

    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

}
