package com.uandcode.example.koin.singlemodule.effects.resources

import androidx.annotation.StringRes

interface Resources {

    fun getString(@StringRes id: Int): String

}