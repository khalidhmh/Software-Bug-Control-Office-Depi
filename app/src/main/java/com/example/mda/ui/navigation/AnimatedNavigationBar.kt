package com.example.mda.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

data class ButtonData(val route: String, val text: String, val icon: ImageVector)

@Composable
fun AnimatedNavigationBar(
    navController: NavController,
    buttons: List<ButtonData>,
    barColor: Color,
    circleColor: Color, // retained for API compatibility
    selectedColor: Color,
    unselectedColor: Color,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val layoutDir = LocalLayoutDirection.current
    val toDisplay = if (layoutDir == LayoutDirection.Rtl) buttons.reversed() else buttons

    // Single surface to avoid the duplicated bar effect
    Surface(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(79.dp),
        color = barColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            toDisplay.forEach { button ->
                val isSelected = button.route == currentRoute
                PillItem(
                    button = button,
                    isSelected = isSelected,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor
                ) {
                    if (!isSelected) {
                        navController.navigate(button.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PillItem(
    button: ButtonData,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    onItemClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor.copy(alpha = 0.15f) else Color.Transparent,
        label = "bgColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        label = "contentColor"
    )

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onItemClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = button.icon,
                contentDescription = button.text,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = Spring.StiffnessLow
                    )
                ),
                exit = fadeOut() + shrinkHorizontally(
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = button.text,
                        color = contentColor,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }
    }
}