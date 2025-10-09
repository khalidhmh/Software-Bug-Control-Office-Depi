package com.example.mda.ui.actordetails.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun SocialIcon(iconRes: Int, onClick: () -> Unit) {
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier
            .size(36.dp)
            .clickable { onClick() }
    )
}