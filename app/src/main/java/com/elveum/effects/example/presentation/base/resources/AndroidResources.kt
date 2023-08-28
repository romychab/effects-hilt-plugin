package com.elveum.effects.example.presentation.base.resources

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidResources @Inject constructor(
    @ApplicationContext private val context: Context
) : Resources {

    override fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

}