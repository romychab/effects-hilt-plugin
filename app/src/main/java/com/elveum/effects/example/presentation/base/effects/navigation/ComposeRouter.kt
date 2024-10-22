package com.elveum.effects.example.presentation.base.effects.navigation

import androidx.activity.ComponentActivity
import androidx.navigation.NavController
import com.elveum.effects.annotations.SideEffect
import com.elveum.effects.example.domain.Cat
import com.elveum.effects.example.presentation.CatDetailsRoute

@SideEffect
class ComposeRouter(
    private val activity: ComponentActivity
) : Router {

    private var navController: NavController? = null

    override fun launchDetails(cat: Cat) {
        navController?.navigate(CatDetailsRoute(cat.id))
    }

    override fun goBack() {
        activity.onBackPressedDispatcher.onBackPressed()
    }

    fun setNavController(navController: NavController) {
        this.navController = navController
    }
}