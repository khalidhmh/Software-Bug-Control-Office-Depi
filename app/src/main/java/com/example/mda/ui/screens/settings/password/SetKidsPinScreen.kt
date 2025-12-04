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
fun SetKidsPinScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val store = remember { KidsSecurityDataStore(context) }
    val currentPin by store.pinFlow.collectAsState(initial = null)
    val qa by store.securityQAFlow.collectAsState(
        initial = KidsSecurityDataStore.SecurityQA(
            q1 = null, q2 = null, q3 = null,
            a1 = null, a2 = null, a3 = null,
            a1Index = null, a2Index = null, a3Index = null
        )
    )

    val scope = rememberCoroutineScope()
    val locManager = remember { LocalizationManager(context) }
    val appLanguage by locManager.currentLanguage.collectAsState(initial = LocalizationManager.Language.ENGLISH)

    var stage by remember { mutableStateOf(1) } // 1-enter, 2-confirm
    var firstPin by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        onTopBarStateChange(
            TopBarState(
                title = if (currentPin.isNullOrEmpty()) locManager.getString(LocalizationKeys.PW_SET_KIDS_PIN, appLanguage) else locManager.getString(LocalizationKeys.PW_CHANGE_KIDS_PIN, appLanguage),
                showBackButton = true
            )
        )
    }

    fun appendDigit(d: Int) {
        if (pin.length < 6) pin += d.toString()
    }
    fun deleteDigit() { if (pin.isNotEmpty()) pin = pin.dropLast(1) }

    LaunchedEffect(pin, stage) {
        if (pin.length == 6) {
            // clear previous error only when user completes another 6 digits
            error = null
            if (stage == 1) {
                firstPin = pin
                pin = ""
                stage = 2
            } else {
                if (firstPin == pin) {
                    scope.launch {
                        store.setPin(pin)
                        // If security questions not set, go set them now
                        val needsQA = qa.q1 == null || qa.q2 == null || qa.q3 == null || qa.a1.isNullOrBlank() || qa.a2.isNullOrBlank() || qa.a3.isNullOrBlank()
                        if (needsQA) {
                            navController.navigate("security_questions_setup") {
                                popUpTo("set_kids_pin") { inclusive = true }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                } else {
                    error = locManager.getString(LocalizationKeys.PW_PINS_MISMATCH_TRY_AGAIN, appLanguage)
                    pin = ""
                    firstPin = ""
                    stage = 1
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
                    text = localizedString(LocalizationKeys.PW_HINT_SET),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Text(
                text = if (stage == 1) localizedString(LocalizationKeys.PW_ENTER_PIN) else localizedString(LocalizationKeys.PW_CONFIRM_PIN),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            PinDots(count = pin.length)
            Spacer(Modifier.height(8.dp))
            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(8.dp))
            PinPad(onDigit = { appendDigit(it) }, onDelete = { deleteDigit() })
        }
    }
}
