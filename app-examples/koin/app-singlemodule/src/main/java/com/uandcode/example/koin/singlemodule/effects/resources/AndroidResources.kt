package com.uandcode.example.koin.singlemodule.effects.resources

import android.content.Context
import androidx.annotation.StringRes

// no need to add HiltEffect, because Resources can use an application
// context and be a singleton
class AndroidResources constructor(
    private val context: Context
) : Resources {

    override fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

}