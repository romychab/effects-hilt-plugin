package com.uandcode.example.hilt.singlemodule.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.uandcode.example.hilt.singlemodule.R

@Composable
fun LikedIcon(
    isLiked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val likeIcon = if (isLiked) R.drawable.ic_favorite else R.drawable.ic_favorite_not
    val likeIconColor = if (isLiked) R.color.active else R.color.inactive
    IconButton(onClick = onToggle, modifier = modifier) {
        Icon(
            painter = painterResource(likeIcon),
            contentDescription = "Like",
            tint = colorResource(likeIconColor),
        )
    }
}