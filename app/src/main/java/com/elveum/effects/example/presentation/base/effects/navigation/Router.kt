package com.elveum.effects.example.presentation.base.effects.navigation

import com.elveum.effects.example.domain.Cat

interface Router {

    fun launchDetails(cat: Cat)

    fun goBack()

}
