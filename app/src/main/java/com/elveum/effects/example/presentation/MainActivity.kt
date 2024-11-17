package com.elveum.effects.example.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elveum.effects.compose.EffectsApp
import com.elveum.effects.compose.getEffect
import com.elveum.effects.example.presentation.base.effects.dialogs.ComposeDialogs
import com.elveum.effects.example.presentation.base.effects.navigation.ComposeRouter
import com.elveum.effects.example.presentation.details.CatDetailsScreen
import com.elveum.effects.example.presentation.list.CatsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EffectsApp {
                CatsApp()
            }
        }
    }
}

@Composable
fun CatsApp() {
    val composeRouter = getEffect<ComposeRouter>()
    val navController = rememberNavController()
    SideEffect {
        composeRouter.setNavController(navController)
    }
    NavHost(
        navController = navController,
        startDestination = CatsRoute,
    ) {
        composable<CatsRoute> { CatsScreen() }
        composable<CatDetailsRoute> { CatDetailsScreen() }
    }
    getEffect<ComposeDialogs>().Dialog()
}