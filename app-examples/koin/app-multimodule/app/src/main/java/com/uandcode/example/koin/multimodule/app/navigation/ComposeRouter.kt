package com.uandcode.example.koin.multimodule.app.navigation

import androidx.navigation.NavHostController
import com.uandcode.effects.koin.annotations.KoinEffect
import com.uandcode.example.koin.multimodule.features.details.CatDetailsRouter
import com.uandcode.example.koin.multimodule.features.list.CatsRouter
import com.uandcode.example.koin.multimodule.features.list.domain.Cat

@KoinEffect
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
