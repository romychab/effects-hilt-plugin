package com.elveum.effects.example.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elveum.effects.compose.MviEffectsApp
import com.elveum.effects.compose.getMviEffect
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
            MviEffectsApp {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CatsApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CatsApp(modifier: Modifier = Modifier) {
    val composeRouter = getMviEffect<ComposeRouter>()
    val navController = rememberNavController()
    SideEffect {
        composeRouter.setNavController(navController)
    }
    NavHost(
        navController = navController,
        startDestination = CatsRoute,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<CatsRoute> { CatsScreen() }
        composable<CatDetailsRoute> { CatDetailsScreen() }
    }
    getMviEffect<ComposeDialogs>().Dialog()
}