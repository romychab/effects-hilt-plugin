package com.elveum.effects.example.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elveum.effects.compose.EffectProvider
import com.elveum.effects.compose.getEffect
import com.elveum.effects.compose.getHostActivity
import com.elveum.effects.example.presentation.base.effects.dialogs.ComposeDialogs
import com.elveum.effects.example.presentation.base.effects.navigation.ComposeRouter
import com.elveum.effects.example.presentation.details.CatDetailsScreen
import com.elveum.effects.example.presentation.list.CatsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // example for non- Jetpack Compose projects how to connect
    // an implementation to a proxy Dialogs instances injected to view-models:
    //   private val dialogs by lazyEffect { ComposeDialogs(context = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dialogs = remember { ComposeDialogs(this) }
            // the first EffectProvider; it connects ComposeDialogs to
            // Dialogs proxy instances injected to view-models when
            // activity is in a STARTED state
            EffectProvider(dialogs) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CatsApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

}

@Composable
fun CatsApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val activity = LocalContext.current.getHostActivity()
    val composeRouter = remember { ComposeRouter(activity, navController) }
    // the second, nested EffectProvider which connects ComposeRouter to
    // Router proxy instances injected to view-models
    EffectProvider(composeRouter) {
        NavHost(
            navController = navController,
            startDestination = CatsRoute,
            modifier = modifier.fillMaxSize(),
        ) {
            composable<CatsRoute> { CatsScreen() }
            composable<CatDetailsRoute> { CatDetailsScreen() }
        }

        // example of using getEffect() function
        getEffect<ComposeDialogs>().Dialog()
    }
}