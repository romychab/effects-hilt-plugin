package com.uandcode.example.hilt.singlemodule.effects.navigation

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.uandcode.effects.hilt.annotations.HiltEffect
import com.uandcode.example.hilt.singlemodule.CatDetailsRoute

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