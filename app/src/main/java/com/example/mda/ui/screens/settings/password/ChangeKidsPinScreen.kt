package com.example.mda.ui.screens.settings.password

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mda.data.datastore.KidsSecurityDataStore
import com.example.mda.ui.navigation.TopBarState
import kotlinx.coroutines.launch
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString
import com.example.mda.localization.LocalizationManager

@Composable
fun ChangeKidsPinScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val store = remember { KidsSecurityDataStore(context) }
    val savedPin by store.pinFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val locManager = remember { LocalizationManager(context) }
    val appLanguage by locManager.currentLanguage.collectAsState(initial = LocalizationManager.Language.ENGLISH)

    var stage by remember { mutableStateOf(0) } // 0=enter old, 1=enter new, 2=confirm new
    var input by remember { mutableStateOf("") }
    var firstNew by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        onTopBarStateChange(TopBarState(title = locManager.getString(LocalizationKeys.PW_CHANGE_KIDS_PIN, appLanguage), showBackButton = true))
    }

    fun appendDigit(d: Int) { if (input.length < 6) input += d.toString() }
    fun deleteDigit() { if (input.isNotEmpty()) input = input.dropLast(1) }

    LaunchedEffect(input, stage, savedPin) {
        if (input.length == 6) {
            // clear previous error only when user completes another 6 digits
            error = null
            when (stage) {
                0 -> { // verify old
                    if (savedPin != null && input == savedPin) {
                        stage = 1
                        input = ""
                    } else {
                        error = locManager.getString(LocalizationKeys.PW_INCORRECT_OLD_PIN, appLanguage)
                        input = ""
                    }
                }
                1 -> { // new
                    firstNew = input
                    input = ""
                    stage = 2
                }
                2 -> { // confirm
                    if (firstNew == input) {
                        scope.launch {
                            store.setPin(input)
                            navController.popBackStack()
                        }
                    } else {
                        error = locManager.getString(LocalizationKeys.PW_PINS_MISMATCH, appLanguage)
                        input = ""
                        firstNew = ""
                        stage = 1
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Text(
                    text = localizedString(LocalizationKeys.PW_HINT_CHANGE),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Text(
                text = when (stage) {
                    0 -> localizedString(LocalizationKeys.PW_ENTER_OLD_PIN)
                    1 -> localizedString(LocalizationKeys.PW_ENTER_NEW_PIN)
                    else -> localizedString(LocalizationKeys.PW_CONFIRM_NEW_PIN)
                },
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            PinDots(count = input.length)
            if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)

            PinPad(onDigit = { appendDigit(it) }, onDelete = { deleteDigit() })
            if (stage == 0) {
                TextButton(onClick = { navController.navigate("security_questions_verify") }) {
                    Text(localizedString(LocalizationKeys.PW_FORGOT_PIN))
                }
            }
        }
    }
}
