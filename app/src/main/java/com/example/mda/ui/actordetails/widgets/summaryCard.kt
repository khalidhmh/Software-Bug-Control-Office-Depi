package com.example.mda.ui.actordetails.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SummaryCard(
    tvShows: String,
    movies: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .width(IntrinsicSize.Max),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E3A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem("TV Shows", tvShows)
            SummaryItem("Movies", movies)
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Color(0xFFAAAAFF),
            fontSize = 16.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}