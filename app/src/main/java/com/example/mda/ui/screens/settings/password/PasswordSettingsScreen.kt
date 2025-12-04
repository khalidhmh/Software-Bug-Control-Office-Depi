package com.example.mda.ui.screens.settings.password

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.datastore.KidsSecurityDataStore
import com.example.mda.ui.navigation.TopBarState
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.material3.TextButton
import com.example.mda.ui.screens.settings.password.PinPad
import com.example.mda.ui.screens.settings.password.PinDots
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString
import com.example.mda.localization.LocalizationManager

@Composable
fun PasswordSettingsScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val store = remember { KidsSecurityDataStore(context) }
    val pin by store.pinFlow.collectAsState(initial = null)
    val lockEnabled by store.lockEnabledFlow.collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    val locManager = remember { LocalizationManager(context) }
    val appLanguage by locManager.currentLanguage.collectAsState(initial = LocalizationManager.Language.ENGLISH)

    LaunchedEffect(Unit) {
        onTopBarStateChange(TopBarState(title = locManager.getString(LocalizationKeys.SETTINGS_PASSWORD, appLanguage), showBackButton = true))
    }

    val snack = remember { SnackbarHostState() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        SnackbarHost(hostState = snack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = localizedString(LocalizationKeys.PW_REQUIRE_PIN_LABEL))
                    Text(text = localizedString(LocalizationKeys.PW_MANAGE_DESC), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = lockEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            if (enabled && pin.isNullOrEmpty()) {
                                snack.showSnackbar(locManager.getString(LocalizationKeys.PW_SET_PIN_FIRST, appLanguage))
                            } else {
                                store.setLockEnabled(enabled)
                            }
                        }
                    }
                )
            }
        }

        Button(
            onClick = {
                if (pin.isNullOrEmpty()) {
                    navController.navigate("set_kids_pin")
                } else {
                    navController.navigate("change_kids_pin")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (pin.isNullOrEmpty()) localizedString(LocalizationKeys.PW_SET_KIDS_PIN) else localizedString(LocalizationKeys.PW_CHANGE_KIDS_PIN)) }

        // Clear PIN action with verification
        if (!pin.isNullOrEmpty()) {
            var showClearDialog by remember { mutableStateOf(false) }
            var input by remember { mutableStateOf("") }
            var error by remember { mutableStateOf<String?>(null) }

            Button(
                onClick = {
                    showClearDialog = true
                    input = ""
                    error = null
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(localizedString(LocalizationKeys.PW_CLEAR_KIDS_PIN)) }

            if (showClearDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDialog = false },
                    title = { Text(localizedString(LocalizationKeys.PW_CLEAR_DIALOG_TITLE)) },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = localizedString(LocalizationKeys.PW_CLEAR_DIALOG_DESC), style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            PinDots(count = input.length)
                            if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(8.dp))
                            PinPad(
                                onDigit = {
                                    if (input.length < 6) input += it.toString()
                                    if (input.length == 6) {
                                        // Clear previous error only when full length reached again
                                        val entered = input
                                        error = null
                                        if (entered == pin) {
                                            scope.launch {
                                                store.clearPin()
                                                store.setLockEnabled(false)
                                                showClearDialog = false
                                            }
                                        } else {
                                            error = locManager.getString(LocalizationKeys.PW_INCORRECT_PIN, appLanguage)
                                            input = ""
                                        }
                                    }
                                },
                                onDelete = { if (input.isNotEmpty()) input = input.dropLast(1) }
                            )
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { showClearDialog = false }) { Text(localizedString(LocalizationKeys.COMMON_BACK)) }
                    }
                )
            }
        }
        }
    }
}
