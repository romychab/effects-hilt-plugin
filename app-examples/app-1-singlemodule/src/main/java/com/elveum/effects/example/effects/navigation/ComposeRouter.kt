package com.elveum.effects.example.effects.navigation

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.elveum.effects.annotations.HiltEffect
import com.elveum.effects.example.CatDetailsRoute

@HiltEffect
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