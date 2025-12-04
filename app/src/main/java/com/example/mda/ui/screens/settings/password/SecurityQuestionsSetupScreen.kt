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
import com.example.mda.localization.LocalizationManager
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString

// Top-level model used by setup and verify screens
data class QAItem(val question: String, val options: List<String>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityQuestionsSetupScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val store = remember { KidsSecurityDataStore(context) }
    val scope = rememberCoroutineScope()

    val locManager = remember { LocalizationManager(context) }
    val appLanguage by locManager.currentLanguage.collectAsState(initial = LocalizationManager.Language.ENGLISH)
    val qaItems = remember(appLanguage) {
        when (appLanguage) {
            LocalizationManager.Language.ARABIC -> listOf(
                QAItem("ما هي لونك المفضل؟", listOf("أحمر", "أزرق", "أخضر", "أصفر")),
                QAItem("ما هو شهر ميلادك؟", listOf("يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر")),
                QAItem("ما هو حيوانك المفضل؟", listOf("قط", "كلب", "طائر", "سمكة")),
                QAItem("ما هو فصلُك المفضل؟", listOf("الربيع", "الصيف", "الخريف", "الشتاء")),
                QAItem("ما هي رياضتك المفضلة؟", listOf("كرة القدم", "كرة السلة", "التنس", "السباحة")),
                QAItem("ما هو نوع الأفلام المفضل لديك؟", listOf("أكشن", "كوميديا", "دراما", "خيال علمي")),
                QAItem("ما هو وقت اليوم المفضل لديك؟", listOf("الصباح", "الظهيرة", "المساء", "الليل")),
                QAItem("ما هو الطقس الذي تفضله؟", listOf("مشمس", "ممطر", "مثلج", "عاصف"))
            )
            LocalizationManager.Language.GERMAN -> listOf(
                QAItem("Was ist deine Lieblingsfarbe?", listOf("Rot", "Blau", "Grün", "Gelb")),
                QAItem("In welchem Monat hast du Geburtstag?", listOf("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember")),
                QAItem("Was ist dein Lieblingstier?", listOf("Katze", "Hund", "Vogel", "Fisch")),
                QAItem("Was ist deine Lieblingsjahreszeit?", listOf("Frühling", "Sommer", "Herbst", "Winter")),
                QAItem("Was ist dein Lieblingssport?", listOf("Fußball", "Basketball", "Tennis", "Schwimmen")),
                QAItem("Welches Filmgenre magst du am liebsten?", listOf("Action", "Komödie", "Drama", "Sci‑Fi")),
                QAItem("Welche Tageszeit bevorzugst du?", listOf("Morgen", "Nachmittag", "Abend", "Nacht")),
                QAItem("Welches Wetter magst du am meisten?", listOf("Sonnig", "Regnerisch", "Schnee", "Windig"))
            )
            else -> listOf(
                QAItem("What is your favorite color?", listOf("Red", "Blue", "Green", "Yellow")),
                QAItem("What is your birth month?", listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")),
                QAItem("What is your favorite animal?", listOf("Cat", "Dog", "Bird", "Fish")),
                QAItem("What is your favorite season?", listOf("Spring", "Summer", "Autumn", "Winter")),
                QAItem("What is your favorite sport?", listOf("Football", "Basketball", "Tennis", "Swimming")),
                QAItem("What is your preferred movie genre?", listOf("Action", "Comedy", "Drama", "Sci‑Fi")),
                QAItem("What time of day do you prefer?", listOf("Morning", "Afternoon", "Evening", "Night")),
                QAItem("What weather do you like most?", listOf("Sunny", "Rainy", "Snowy", "Windy"))
            )
        }
    }

    var q1Index by remember { mutableStateOf(-1) }
    var q2Index by remember { mutableStateOf(-1) }
    var q3Index by remember { mutableStateOf(-1) }

    var a1Index by remember { mutableStateOf(-1) }
    var a2Index by remember { mutableStateOf(-1) }
    var a3Index by remember { mutableStateOf(-1) }

    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        onTopBarStateChange(TopBarState(title = locManager.getString(LocalizationKeys.SQ_TITLE, appLanguage), showBackButton = true))
    }

    fun save() {
        error = null
        if (q1Index < 0 || q2Index < 0 || q3Index < 0) {
            error = locManager.getString(LocalizationKeys.SQ_SELECT_ALL_QUESTIONS, appLanguage)
            return
        }
        if (a1Index < 0 || a2Index < 0 || a3Index < 0) {
            error = locManager.getString(LocalizationKeys.SQ_SELECT_ANSWER_EACH, appLanguage)
            return
        }
        if (q1Index == q2Index || q1Index == q3Index || q2Index == q3Index) {
            error = locManager.getString(LocalizationKeys.SQ_QUESTIONS_MUST_DIFFER, appLanguage)
            return
        }
        scope.launch {
            val ans1 = qaItems[q1Index].options[a1Index]
            val ans2 = qaItems[q2Index].options[a2Index]
            val ans3 = qaItems[q3Index].options[a3Index]
            store.setSecurityQAWithIndices(
                q1 = q1Index, q2 = q2Index, q3 = q3Index,
                a1Index = a1Index, a2Index = a2Index, a3Index = a3Index,
                a1Text = ans1, a2Text = ans2, a3Text = ans3
            )
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(localizedString(LocalizationKeys.SQ_HEADER_SETUP), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(
                    localizedString(LocalizationKeys.SQ_SUBTEXT_SETUP),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        QuestionInput(
            label = localizedString(LocalizationKeys.SQ_QUESTION_1),
            qaItems = qaItems,
            selectedQuestionIndex = q1Index,
            onQuestionSelected = { q1Index = it; a1Index = -1 },
            disabledIndices = setOf(q2Index, q3Index).filter { it >= 0 }.toSet(),
            selectedAnswerIndex = a1Index,
            onAnswerSelected = { a1Index = it }
        )
        QuestionInput(
            label = localizedString(LocalizationKeys.SQ_QUESTION_2),
            qaItems = qaItems,
            selectedQuestionIndex = q2Index,
            onQuestionSelected = { q2Index = it; a2Index = -1 },
            disabledIndices = setOf(q1Index, q3Index).filter { it >= 0 }.toSet(),
            selectedAnswerIndex = a2Index,
            onAnswerSelected = { a2Index = it }
        )
        QuestionInput(
            label = localizedString(LocalizationKeys.SQ_QUESTION_3),
            qaItems = qaItems,
            selectedQuestionIndex = q3Index,
            onQuestionSelected = { q3Index = it; a3Index = -1 },
            disabledIndices = setOf(q1Index, q2Index).filter { it >= 0 }.toSet(),
            selectedAnswerIndex = a3Index,
            onAnswerSelected = { a3Index = it }
        )

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }

        Button(onClick = { save() }, modifier = Modifier.fillMaxWidth()) {
            Text(localizedString(LocalizationKeys.BTN_SAVE))
        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionInput(
    label: String,
    qaItems: List<QAItem>,
    selectedQuestionIndex: Int,
    onQuestionSelected: (Int) -> Unit,
    disabledIndices: Set<Int>,
    selectedAnswerIndex: Int,
    onAnswerSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        var qExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = qExpanded, onExpandedChange = { qExpanded = !qExpanded }) {
            TextField(
                readOnly = true,
                value = if (selectedQuestionIndex >= 0) qaItems[selectedQuestionIndex].question else "Select question",
                onValueChange = {},
                label = { Text("Select question") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = qExpanded, onDismissRequest = { qExpanded = false }) {
                qaItems.forEachIndexed { index, item ->
                    val isDisabled = index in disabledIndices
                    DropdownMenuItem(
                        enabled = !isDisabled,
                        text = { Text(item.question) },
                        onClick = {
                            if (!isDisabled) {
                                onQuestionSelected(index)
                                qExpanded = false
                            }
                        }
                    )
                }
            }
        }

        var aExpanded by remember { mutableStateOf(false) }
        val answersEnabled = selectedQuestionIndex >= 0
        ExposedDropdownMenuBox(expanded = aExpanded && answersEnabled, onExpandedChange = { if (answersEnabled) aExpanded = !aExpanded }) {
            TextField(
                readOnly = true,
                enabled = answersEnabled,
                value = if (answersEnabled && selectedAnswerIndex >= 0) qaItems[selectedQuestionIndex].options[selectedAnswerIndex] else "Select answer",
                onValueChange = {},
                label = { Text("Select answer") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            if (answersEnabled) {
                ExposedDropdownMenu(expanded = aExpanded, onDismissRequest = { aExpanded = false }) {
                    qaItems[selectedQuestionIndex].options.forEachIndexed { idx, opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                onAnswerSelected(idx)
                                aExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
