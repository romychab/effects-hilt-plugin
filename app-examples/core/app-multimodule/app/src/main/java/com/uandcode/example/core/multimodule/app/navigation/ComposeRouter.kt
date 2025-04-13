package com.uandcode.example.core.multimodule.app.navigation

import androidx.navigation.NavHostController
import com.uandcode.effects.core.annotations.EffectClass
import com.uandcode.example.core.multimodule.features.details.CatDetailsRouter
import com.uandcode.example.core.multimodule.features.list.CatsRouter
import com.uandcode.example.core.multimodule.features.list.domain.Cat

@EffectClass
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
