package com.elveum.effects.example.presentation.base.effects.navigation

import androidx.activity.ComponentActivity
import androidx.navigation.NavHostController
import com.elveum.effects.annotations.CustomEffect
import com.elveum.effects.example.domain.Cat
import com.elveum.effects.example.presentation.CatDetailsRoute
import dagger.hilt.android.components.ActivityRetainedComponent

@CustomEffect(
    target = Router::class,
    installIn = ActivityRetainedComponent::class
)
class ComposeRouter(
    private val activity: ComponentActivity,
    val navController: NavHostController,
) : Router {

    override fun launchDetails(cat: Cat) {
        navController.navigate(CatDetailsRoute(cat.id))
    }

    override fun goBack() {
        activity.onBackPressedDispatcher.onBackPressed()
    }

}