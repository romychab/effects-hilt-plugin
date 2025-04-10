package com.elveum.effects.example.effects.resources

import androidx.annotation.StringRes

interface Resources {

    fun getString(@StringRes id: Int): String

}