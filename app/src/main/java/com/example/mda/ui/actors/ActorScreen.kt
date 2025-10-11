package com.example.mda.ui.actors

import androidx.compose.foundation.ExperimentalFoundationApi

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ActorsScreen(
    navController: NavHostController,
    viewModel: ActorViewModel = viewModel(factory = ActorViewModelFactory()),
) {
    val uiState by viewModel.state.collectAsState()
    val viewType by viewModel.viewType.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF101528),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Column {
                        Text(
                            text = "People",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleViewType() }) {
                        Icon(
                            imageVector = if (viewType == ViewType.GRID) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = "Toggle View",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) {  innerPadding ->
        when (val state = uiState) {

            is ActorUiState.Loading -> {
            }

            is ActorUiState.Success -> {
                ActorsView(
                    actors = state.actors,
                    viewModel = viewModel,
                    viewType = viewType,
                    navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is ActorUiState.Error -> {
                ActorErrorScreen(
                    errorType = state.type,
                    onRetry = { viewModel.loadMoreActors() }
                )
            }
        }
    }
}