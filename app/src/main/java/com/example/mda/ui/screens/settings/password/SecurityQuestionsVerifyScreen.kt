package com.example.mda.ui.screens.settings.password

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mda.localization.LocalizationManager
import androidx.navigation.NavController
import com.example.mda.data.datastore.KidsSecurityDataStore
import com.example.mda.ui.navigation.TopBarState
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityQuestionsVerifyScreen(
    navController: NavController,
    onTopBarStateChange: (TopBarState) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val store = remember { KidsSecurityDataStore(context) }
    val qa by store.securityQAFlow.collectAsState(
        initial = KidsSecurityDataStore.SecurityQA(
            q1 = null, q2 = null, q3 = null,
            a1 = null, a2 = null, a3 = null,
            a1Index = null, a2Index = null, a3Index = null
        )
    )

    val locManager = remember { LocalizationManager(context) }
    val appLanguage by locManager.currentLanguage.collectAsState(initial = LocalizationManager.Language.ENGLISH)
    val qaItems = remember(appLanguage) {
        when (appLanguage) {
            LocalizationManager.Language.ARABIC -> listOf(
                QAItem("ما هو لونك المفضل؟", listOf("أحمر", "أزرق", "أخضر", "أصفر")),
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
                QAItem("Welches Filmgenre magst du am liebsten?", listOf("Action", "Komödie", "Drama", "Sci-Fi")),
                QAItem("Welche Tageszeit bevorzugst du?", listOf("Morgen", "Nachmittag", "Abend", "Nacht")),
                QAItem("Welches Wetter magst du am meisten?", listOf("Sonnig", "Regnerisch", "Schnee", "Windig"))
            )
            else -> listOf(
                QAItem("What is your favorite color?", listOf("Red", "Blue", "Green", "Yellow")),
                QAItem("What is your birth month?", listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")),
                QAItem("What is your favorite animal?", listOf("Cat", "Dog", "Bird", "Fish")),
                QAItem("What is your favorite season?", listOf("Spring", "Summer", "Autumn", "Winter")),
                QAItem("What is your favorite sport?", listOf("Football", "Basketball", "Tennis", "Swimming")),
                QAItem("What is your preferred movie genre?", listOf("Action", "Comedy", "Drama", "Sci-Fi")),
                QAItem("What time of day do you prefer?", listOf("Morning", "Afternoon", "Evening", "Night")),
                QAItem("What weather do you like most?", listOf("Sunny", "Rainy", "Snowy", "Windy"))
            )
        }
    }

    var a1Index by remember { mutableStateOf(-1) }
    var a2Index by remember { mutableStateOf(-1) }
    var a3Index by remember { mutableStateOf(-1) }
    var error by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onTopBarStateChange(TopBarState(title = locManager.getString(LocalizationKeys.SQ_VERIFY_TITLE, appLanguage), showBackButton = true))
    }

    val qIndices = remember(qa.q1, qa.q2, qa.q3) {
        val list = listOfNotNull(qa.q1, qa.q2, qa.q3)
        list.shuffled() // randomize order when screen created
    }

    fun verify() {
        error = null
        if (qIndices.size != 3 || qa.a1.isNullOrBlank() || qa.a2.isNullOrBlank() || qa.a3.isNullOrBlank()) {
            error = locManager.getString(LocalizationKeys.SQ_NOT_SET, appLanguage)
            return
        }
        if (a1Index < 0 || a2Index < 0 || a3Index < 0) {
            error = locManager.getString(LocalizationKeys.SQ_PLEASE_SELECT_EVERY_ANSWER, appLanguage)
            return
        }
        val indexMap = listOf(qa.q1!!, qa.q2!!, qa.q3!!)
        val expectedIndexForDisplayed: List<Int?> = qIndices.map { idx ->
            val pos = indexMap.indexOf(idx)
            when (pos) {
                0 -> qa.a1Index
                1 -> qa.a2Index
                else -> qa.a3Index
            }
        }

        val selectedIndices = listOf(a1Index, a2Index, a3Index)

        val ok = if (expectedIndexForDisplayed.all { it != null }) {
            // Compare by indices (language-independent)
            expectedIndexForDisplayed.zip(selectedIndices).all { (expected, selected) -> expected == selected }
        } else {
            // Fallback: compare by text (legacy entries)
            val answersStored = listOf(qa.a1.orEmpty(), qa.a2.orEmpty(), qa.a3.orEmpty())
            val expectedTextForDisplayed = qIndices.map { idx ->
                val pos = indexMap.indexOf(idx)
                answersStored[pos]
            }
            val selectedText = listOf(
                qaItems[qIndices[0]].options.getOrNull(a1Index).orEmpty(),
                qaItems[qIndices[1]].options.getOrNull(a2Index).orEmpty(),
                qaItems[qIndices[2]].options.getOrNull(a3Index).orEmpty()
            )
            expectedTextForDisplayed.zip(selectedText).all { (expected, input) ->
                expected.trim().equals(input.trim(), ignoreCase = true)
            }
        }
        if (ok) {
            showResetDialog = true
        } else {
            error = locManager.getString(LocalizationKeys.SQ_ANSWERS_NOT_MATCH, appLanguage)
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
                Text(localizedString(LocalizationKeys.SQ_HEADER_VERIFY), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = localizedString(LocalizationKeys.SQ_HINT_VERIFY),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (qIndices.size == 3) {
            MultipleChoiceField(
                label = qaItems[qIndices[0]].question,
                options = qaItems[qIndices[0]].options,
                selectedIndex = a1Index,
                onSelected = { a1Index = it }
            )
            MultipleChoiceField(
                label = qaItems[qIndices[1]].question,
                options = qaItems[qIndices[1]].options,
                selectedIndex = a2Index,
                onSelected = { a2Index = it }
            )
            MultipleChoiceField(
                label = qaItems[qIndices[2]].question,
                options = qaItems[qIndices[2]].options,
                selectedIndex = a3Index,
                onSelected = { a3Index = it }
            )
        } else {
            Text(localizedString(LocalizationKeys.SQ_NOT_SET))
        }

        if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
        Button(onClick = { verify() }, modifier = Modifier.fillMaxWidth()) { Text(localizedString(LocalizationKeys.SQ_BTN_VERIFY)) }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(localizedString(LocalizationKeys.SQ_DIALOG_RESET_TITLE)) },
            text = { Text(localizedString(LocalizationKeys.SQ_DIALOG_RESET_TEXT)) },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    navController.navigate("security_questions_setup")
                }) { Text(localizedString(LocalizationKeys.SQ_DIALOG_YES_UPDATE)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    navController.navigate("set_kids_pin")
                }) { Text(localizedString(LocalizationKeys.SQ_DIALOG_NO_CONTINUE)) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MultipleChoiceField(label: String, options: List<String>, selectedIndex: Int, onSelected: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                readOnly = true,
                value = if (selectedIndex >= 0) options[selectedIndex] else "Select answer",
                onValueChange = {},
                label = { Text("Select answer") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEachIndexed { idx, opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = {
                            onSelected(idx)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
