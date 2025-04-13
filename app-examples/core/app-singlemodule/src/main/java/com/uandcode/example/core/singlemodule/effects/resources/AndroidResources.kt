package com.uandcode.example.core.singlemodule.effects.resources

import android.content.Context
import androidx.annotation.StringRes

// no need to add EffectClass, because Resources can use an application
// context and be a singleton
class AndroidResources(
    private val context: Context
) : Resources {

    override fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

}