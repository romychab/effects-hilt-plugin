package com.uandcode.example.hilt.multimodule.app.navigation

import androidx.navigation.NavHostController
import com.uandcode.example.hilt.multimodule.features.list.domain.Cat
import com.uandcode.example.hilt.multimodule.features.details.CatDetailsRouter
import com.uandcode.example.hilt.multimodule.features.list.CatsRouter
import com.uandcode.effects.hilt.annotations.HiltEffect

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
