package com.elveum.effects.example.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elveum.effects.core.AttachSideEffects
import com.elveum.effects.example.presentation.base.effects.dialogs.ComposeDialogs
import com.elveum.effects.example.presentation.base.effects.navigation.ComposeRouter
import com.elveum.effects.example.presentation.details.CatDetailsScreen
import com.elveum.effects.example.presentation.list.CatsScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var attachSideEffects: AttachSideEffects

    @Inject
    lateinit var composeRouter: ComposeRouter

    @Inject
    lateinit var composeDialogs: ComposeDialogs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatsApp(
                onNavController = composeRouter::setNavController,
            )
            composeDialogs.Dialog()
        }
    }
}

@Composable
fun CatsApp(
    onNavController: (NavController) -> Unit,
) {
    val navController = rememberNavController()
    SideEffect {
        onNavController(navController)
    }
    NavHost(
        navController = navController,
        startDestination = CatsRoute,
    ) {
        composable<CatsRoute> { CatsScreen() }
        composable<CatDetailsRoute> { CatDetailsScreen() }
    }
}