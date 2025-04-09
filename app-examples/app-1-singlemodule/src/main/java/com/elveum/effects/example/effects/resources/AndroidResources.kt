package com.elveum.effects.example.effects.resources

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// no need to add HiltEffect, because Resources can use an application
// context and be a singleton
@Singleton
class AndroidResources @Inject constructor(
    @ApplicationContext private val context: Context
) : Resources {

    override fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

}