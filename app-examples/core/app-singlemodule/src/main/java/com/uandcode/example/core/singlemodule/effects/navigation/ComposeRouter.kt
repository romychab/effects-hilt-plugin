package com.uandcode.example.core.singlemodule.effects.navigation

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.example.core.singlemodule.CatDetailsRoute

@EffectClass
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