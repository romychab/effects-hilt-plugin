package com.uandcode.example.hilt.singlemodule.effects.resources

import androidx.annotation.StringRes

interface Resources {

    fun getString(@StringRes id: Int): String

}