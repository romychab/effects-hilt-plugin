package com.uandcode.example.koin.singlemodule.effects.navigation

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.uandcode.effects.koin.annotations.KoinEffect
import com.uandcode.example.koin.singlemodule.CatDetailsRoute

@KoinEffect
@Stable
class ComposeRouter(
    private val navController: NavHostController,
) : Router {

    override fun launchDetails(catId: Long) {
        navController.navigate(CatDetailsRoute(catId))
    }

    override fun goBack() {
        navController.navigateUp()
    }

}