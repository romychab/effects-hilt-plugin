@file:Suppress("unused")

package com.uandcode.example.hilt.singlemodule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uandcode.effects.hilt.compose.EffectProvider
import com.uandcode.effects.hilt.compose.getEffect
import com.uandcode.effects.hilt.lazyEffect
import com.uandcode.example.hilt.singlemodule.effects.dialogs.ComposeDialogs
import com.uandcode.example.hilt.singlemodule.effects.navigation.ComposeRouter
import com.uandcode.example.hilt.singlemodule.effects.toasts.AndroidToasts
import com.uandcode.example.hilt.singlemodule.presentation.details.CatDetailsScreen
import com.uandcode.example.hilt.singlemodule.presentation.list.CatsScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 1a. Example for non- Jetpack Compose projects how to connect
    //     a ToastsImpl implementation to a Toasts interface injected
    //     to view-models:
    private val toasts by lazyEffect { AndroidToasts(context = this) }

    // 1b. Another way for non- Jetpack Compose projects
    //     is to use EffectController, but you need manually
    //     call start() and stop() methods:
    // @Inject
    // lateinit var toastsController: EffectController<AndroidToasts>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 2. Example for Jetpack Compose projects how to connect
            //    ComposeDialogs implementation to Dialogs interface
            //    injected to view-models:
            val dialogs = remember { ComposeDialogs() }
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
    // 3. Example of nesting EffectProviders. You cad add one EffectProvider
    //    into another EffectProvider.
    val navController = rememberNavController()
    val composeRouter = remember { ComposeRouter(navController) }
    EffectProvider(composeRouter) {
        NavHost(
            navController = navController,
            startDestination = CatsRoute,
            modifier = modifier.fillMaxSize(),
        ) {
            composable<CatsRoute> { CatsScreen() }
            composable<CatDetailsRoute> { CatDetailsScreen() }
        }
        // 4. Example of using getEffect() function to get the instance
        //    of the effect you have provided in the EffectProvider.
        getEffect<ComposeDialogs>().Dialog()
    }

}