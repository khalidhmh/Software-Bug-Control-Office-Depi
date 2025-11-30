package com.example.mda.ui.screens.auth

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    darkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var hasCompletedAuth by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }  // ✅ Add this flag

    // Theme colors matching MainActivity
    val topBarText = if (darkTheme) Color.White else Color.Black

    LaunchedEffect(Unit) {
        // Start the authentication flow
        viewModel.startAuthentication()
    }

    // ✅ Handle navigation separately with a flag to prevent double navigation
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated && !hasNavigated) {
            hasNavigated = true
            navController.navigate("profile") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Login with TMDb", color = topBarText) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = topBarText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (darkTheme) Color(0xFF032541) else Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding()
                )
        ) {
            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Authenticating...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please make sure you approved the authentication in your browser.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                hasCompletedAuth = false
                                hasNavigated = false  // ✅ Reset navigation flag
                                viewModel.startAuthentication()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
                uiState.authUrl != null && !hasCompletedAuth && !uiState.isAuthenticated -> {
                    // ✅ Added !uiState.isAuthenticated check
                    TMDbWebView(
                        url = uiState.authUrl!!,
                        onAuthComplete = { approved ->
                            if (approved && !hasCompletedAuth) {
                                hasCompletedAuth = true
                                scope.launch {
                                    // Add a delay to ensure TMDb processes the approval
                                    delay(1500)
                                    viewModel.completeAuthentication()
                                }
                            } else if (!approved) {
                                // User denied authentication
                                navController.popBackStack()
                            }
                        }
                    )
                }
                // ✅ Removed the isAuthenticated case here - it's handled by LaunchedEffect above
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TMDbWebView(
    url: String,
    onAuthComplete: (Boolean) -> Unit
) {
    var hasTriggeredCallback by remember { mutableStateOf(false) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.userAgentString = settings.userAgentString + " TMDbApp/1.0"

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val loadUrl = request?.url?.toString() ?: return false

                        // Don't handle if we already triggered the callback
                        if (hasTriggeredCallback) {
                            return true
                        }

                        // Check if user completed authentication
                        when {
                            // User approved - TMDb redirects away from /authenticate/
                            loadUrl.startsWith("https://www.themoviedb.org/") &&
                                    !loadUrl.contains("/authenticate/") &&
                                    !loadUrl.contains("/login") &&
                                    !loadUrl.contains("/signup") -> {
                                hasTriggeredCallback = true
                                onAuthComplete(true)
                                return true
                            }
                            // Handle explicit deny
                            loadUrl.contains("denied") || loadUrl.contains("cancel") -> {
                                hasTriggeredCallback = true
                                onAuthComplete(false)
                                return true
                            }
                        }
                        return false
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        // Don't handle if we already triggered the callback
                        if (hasTriggeredCallback) {
                            return
                        }

                        // Check if we've successfully navigated away from authenticate page
                        // This happens after user clicks "Approve"
                        if (url != null &&
                            url.startsWith("https://www.themoviedb.org/") &&
                            !url.contains("/authenticate/") &&
                            !url.contains("/signup") &&
                            !url.contains("/login")) {
                            hasTriggeredCallback = true
                            onAuthComplete(true)
                        }
                    }
                }
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}