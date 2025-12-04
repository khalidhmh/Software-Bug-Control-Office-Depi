package com.example.mda.ui.screens.settings.password

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PinDots(count: Int, max: Int = 6) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(max) { i ->
            val filled = i < count
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
fun NumericKey(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun PinPad(onDigit: (Int) -> Unit, onDelete: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        @Composable
        fun row(a: Int, b: Int, c: Int) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                NumericKey(a.toString()) { onDigit(a) }
                NumericKey(b.toString()) { onDigit(b) }
                NumericKey(c.toString()) { onDigit(c) }
            }
        }
        row(1,2,3)
        row(4,5,6)
        row(7,8,9)
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.size(72.dp))
            NumericKey("0") { onDigit(0) }
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .clickable { onDelete() }
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("⌫", color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
