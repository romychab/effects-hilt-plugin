package com.elveum.effects.example.presentation.base.effects.navigation

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.elveum.effects.annotations.SideEffect
import com.elveum.effects.example.R
import com.elveum.effects.example.domain.Cat
import com.elveum.effects.example.presentation.list.CatsFragmentDirections

@SideEffect
class NavComponentRouter(
    private val activity: FragmentActivity,
) : Router {

    private val navController: NavController get() {
        val fragment = activity.supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        return fragment.navController
    }

    override fun launchDetails(cat: Cat) {
        val direction = CatsFragmentDirections.actionToCatDetails(cat.id)
        navController.navigate(direction)
    }

    override fun goBack() {
        navController.popBackStack()
    }

}
