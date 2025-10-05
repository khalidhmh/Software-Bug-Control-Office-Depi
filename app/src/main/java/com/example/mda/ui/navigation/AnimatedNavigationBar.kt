package com.example.mda.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlin.math.*

data class ButtonData(val route: String, val text: String, val icon: ImageVector)

@Composable
fun AnimatedNavigationBar(
    navController: NavController,
    buttons: List<ButtonData>,
    barColor: Color,
    circleColor: Color,
    selectedColor: Color,
    unselectedColor: Color,
) {
    val circleRadius = 26.dp
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    var barSize by remember { mutableStateOf(IntSize(0, 0)) }

    // 🧭 الملاحظة الحالية من NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        val index = buttons.indexOfFirst { it.route == currentRoute }
        if (index != -1) selectedItem = index
    }

    val offsetStep = remember(barSize) { barSize.width.toFloat() / (buttons.size * 2) }
    val offset = remember(selectedItem, offsetStep) { offsetStep + selectedItem * 2 * offsetStep }
    val circleRadiusPx = LocalDensity.current.run { circleRadius.toPx().toInt() }

    val offsetTransition = updateTransition(offset, label = "offset transition")
    val animation = spring<Float>(dampingRatio = 0.5f, stiffness = Spring.StiffnessVeryLow)

    val cutoutOffset by offsetTransition.animateFloat(
        transitionSpec = { if (initialState == 0f) snap() else animation },
        label = "cutout offset"
    ) { it }

    val circleOffset by offsetTransition.animateIntOffset(
        transitionSpec = { if (initialState == 0f) snap() else spring(animation.dampingRatio, animation.stiffness) },
        label = "circle offset"
    ) { IntOffset(it.toInt() - circleRadiusPx, -circleRadiusPx) }

    val barShape = remember(cutoutOffset) {
        BarShape(offset = cutoutOffset, circleRadius = circleRadius, cornerRadius = 25.dp)
    }

    Box {
        Circle(
            modifier = Modifier
                .offset { circleOffset }
                .zIndex(1f),
            color = circleColor,
            radius = circleRadius,
            button = buttons[selectedItem],
            iconColor = selectedColor,
        )

        Row(
            modifier = Modifier
                .onPlaced { barSize = it.size }
                .graphicsLayer {
                    shape = barShape
                    clip = true
                }
                .fillMaxWidth()
                .background(barColor)
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            buttons.forEachIndexed { index, button ->
                val isSelected = index == selectedItem
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        selectedItem = index
                        navController.navigate(button.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        val alpha by animateFloatAsState(
                            targetValue = if (isSelected) 0f else 1f, label = "Navbar item icon"
                        )
                        Icon(
                            imageVector = button.icon,
                            contentDescription = button.text,
                            modifier = Modifier.alpha(alpha)
                        )
                    },
                    label = { Text(button.text) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedColor,
                        selectedTextColor = selectedColor,
                        unselectedIconColor = unselectedColor,
                        unselectedTextColor = unselectedColor,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

private class BarShape(
    private val offset: Float,
    private val circleRadius: Dp,
    private val cornerRadius: Dp,
    private val circleGap: Dp = 5.dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(getPath(size, density))
    }

    private fun getPath(size: Size, density: Density): Path {
        val cutoutCenterX = offset
        val cutoutRadius = density.run { (circleRadius + circleGap).toPx() }
        val cornerRadiusPx = density.run { cornerRadius.toPx() }
        val cornerDiameter = cornerRadiusPx * 2

        return Path().apply {
            val cutoutEdgeOffset = cutoutRadius * 1.5f
            val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
            val cutoutRightX = cutoutCenterX + cutoutEdgeOffset

            moveTo(0f, size.height)
            if (cutoutLeftX > 0) {
                val realLeftCornerDiameter = if (cutoutLeftX >= cornerRadiusPx)
                    cornerDiameter else cutoutLeftX * 2
                arcTo(
                    Rect(0f, 0f, realLeftCornerDiameter, realLeftCornerDiameter),
                    180f, 90f, false
                )
            }
            lineTo(cutoutLeftX, 0f)
            cubicTo(
                cutoutCenterX - cutoutRadius, 0f,
                cutoutCenterX - cutoutRadius, cutoutRadius,
                cutoutCenterX, cutoutRadius
            )
            cubicTo(
                cutoutCenterX + cutoutRadius, cutoutRadius,
                cutoutCenterX + cutoutRadius, 0f,
                cutoutRightX, 0f
            )
            if (cutoutRightX < size.width) {
                val realRightCornerDiameter = if (cutoutRightX <= size.width - cornerRadiusPx)
                    cornerDiameter else (size.width - cutoutRightX) * 2
                arcTo(
                    Rect(
                        size.width - realRightCornerDiameter, 0f,
                        size.width, realRightCornerDiameter
                    ),
                    -90f, 90f, false
                )
            }
            lineTo(size.width, size.height)
            close()
        }
    }
}

@Composable
private fun Circle(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    radius: Dp,
    button: ButtonData,
    iconColor: Color,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(radius * 2)
            .clip(CircleShape)
            .background(color),
    ) {
        AnimatedContent(targetState = button.icon, label = "Bottom bar circle icon") { targetIcon ->
            Icon(targetIcon, button.text, tint = iconColor)
        }
    }
}
