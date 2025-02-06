package com.elveum.effects.example.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elveum.effects.compose.v2.EffectProvider
import com.elveum.effects.compose.v2.getEffect
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
            val navController = rememberNavController()
            EffectProvider(
                ComposeDialogs(this),
                ComposeRouter(this, navController)
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CatsApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

}

@Composable
fun CatsApp(modifier: Modifier = Modifier) {
    val composeRouter = getEffect<ComposeRouter>()
    NavHost(
        navController = composeRouter.navController,
        startDestination = CatsRoute,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<CatsRoute> { CatsScreen() }
        composable<CatDetailsRoute> { CatDetailsScreen() }
    }
    getEffect<ComposeDialogs>().Dialog()
}