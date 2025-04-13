package com.uandcode.example.core.singlemodule.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.uandcode.example.core.singlemodule.R
import com.uandcode.example.core.singlemodule.domain.Cat
import com.uandcode.example.core.singlemodule.ui.ContainerView
import com.uandcode.example.core.singlemodule.ui.LikedIcon

@Composable
fun CatDetailsScreen() {
    val viewModel = viewModel<CatDetailsViewModel> {
        CatDetailsViewModel(createSavedStateHandle())
    }
    val catContainer by viewModel.catFlow.collectAsState()
    ContainerView(
        modifier = Modifier.fillMaxSize(),
        container = catContainer
    ) { cat ->
        CatDetailsContent(
            cat = cat,
            onAction = viewModel::processAction,
        )
    }
}

@Composable
fun CatDetailsContent(
    cat: Cat,
    onAction: (CatDetailsAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box {
            AsyncImage(
                model = cat.image,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            LikedIcon(
                isLiked = cat.isLiked,
                onToggle = { onAction(CatDetailsAction.ToggleLike) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(Color.White, CircleShape)
                    .border(2.dp, colorResource(R.color.inactive), CircleShape),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = cat.name,
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = cat.details,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onAction(CatDetailsAction.GoBack) }) {
            Text(stringResource(R.string.go_back))
        }
    }
}
