package com.uandcode.example.koin.singlemodule.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.elveum.container.Container

@Composable
fun <T : Any> ContainerView(
    modifier: Modifier = Modifier,
    container: Container<T>?,
    content: @Composable (T) -> Unit,
) {
    val finalContainer = container ?: Container.Pending
    when (finalContainer) {
        Container.Pending -> {
            Box(modifier) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
        is Container.Success -> content(finalContainer.value)
        is Container.Error -> TODO()
    }
}