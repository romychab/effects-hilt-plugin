package com.uandcode.example.hilt.multimodule.app

import android.content.Context
import androidx.annotation.StringRes
import com.uandcode.example.hilt.multimodule.effects.api.Resources
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidResources @Inject constructor(
    @ApplicationContext private val context: Context
) : Resources {

    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

}
