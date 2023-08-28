package com.elveum.effects.example.presentation.base.resources

import androidx.annotation.StringRes

interface Resources {

    fun getString(@StringRes id: Int): String

}