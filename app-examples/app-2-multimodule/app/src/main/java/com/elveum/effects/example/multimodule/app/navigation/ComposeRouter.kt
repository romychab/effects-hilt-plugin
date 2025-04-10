package com.elveum.effects.example.multimodule.app.navigation

import androidx.navigation.NavHostController
import com.elveum.effects.annotations.HiltEffect
import com.elveum.effects.example.features.details.CatDetailsRouter
import com.elveum.effects.example.features.list.CatsRouter
import com.elveum.effects.example.features.list.domain.Cat

@HiltEffect
class ComposeRouter(
    private val navController: NavHostController,
) : CatsRouter, CatDetailsRouter {

    override fun launchDetails(cat: Cat) {
        navController.navigate(CatDetailsRoute(cat.id))
    }

    override fun goBack() {
        navController.navigateUp()
    }

}
