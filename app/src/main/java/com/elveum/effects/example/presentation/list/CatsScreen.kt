package com.elveum.effects.example.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.elveum.container.Container
import com.elveum.effects.example.R
import com.elveum.effects.example.domain.Cat
import com.elveum.effects.example.presentation.components.ContainerView
import com.elveum.effects.example.presentation.components.LikedIcon

@Composable
fun CatsScreen() {
    val viewModel = hiltViewModel<CatsViewModel>()
    val container by viewModel.catsLiveData.observeAsState(Container.Pending)
    ContainerView(
        modifier = Modifier.fillMaxSize(),
        container = container,
    ) { cats ->
        CatsContent(
            cats = cats,
            onLike = viewModel::toggleLike,
            onDelete = viewModel::delete,
            onCatChosen = viewModel::launchDetails,
        )
    }
}

@Composable
fun CatsContent(
    cats: List<Cat>,
    onLike: (Cat) -> Unit,
    onDelete: (Cat) -> Unit,
    onCatChosen: (Cat) -> Unit,
) {
    LazyColumn {
        items(
            items = cats,
            key = { cat -> cat.id }
        ) { cat ->
            CatItem(
                cat = cat,
                onLike = { onLike(cat) },
                onDelete = { onDelete(cat) },
                modifier = Modifier
                    .clickable { onCatChosen(cat) }
                    .animateItem(),
            )
        }
    }
}

@Composable
fun CatItem(
    cat: Cat,
    onLike: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = cat.image,
            contentDescription = null,
            modifier = Modifier.size(64.dp).clip(CircleShape).align(Alignment.Top),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = cat.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = cat.details,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        LikedIcon(
            isLiked = cat.isLiked,
            onToggle = onLike,
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "Delete",
                tint = colorResource(R.color.inactive),
            )
        }
    }
}