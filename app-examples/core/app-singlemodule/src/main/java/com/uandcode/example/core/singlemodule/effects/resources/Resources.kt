package com.uandcode.example.core.singlemodule.effects.resources

import androidx.annotation.StringRes

interface Resources {

    fun getString(@StringRes id: Int): String

}