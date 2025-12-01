# 🔧 Localization Implementation Guide

## Quick Start

### 1. Update Navigation (MdaNavHost.kt)

Add the language settings route to your navigation:

```kotlin
import com.example.mda.ui.Settings.LanguageSettingsScreen

@Composable
fun MdaNavHost(
    navController: NavHostController,
    onTopBarStateChange: (TopBarState) -> Unit,
    authViewModel: AuthViewModel?,
    favoritesViewModel: FavoritesViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // ... existing routes ...
        
        composable("language_settings") {
            LanguageSettingsScreen(
                navController = navController,
                onTopBarStateChange = onTopBarStateChange
            )
        }
        
        // ... other routes ...
    }
}
```

### 2. Update Settings Screen (SettingsScreen.kt)

Replace the hardcoded "Language" text with localized version:

**Before:**
```kotlin
SettingsItem(Icons.Default.Language, "Language") { 
    navController.navigate("language_settings") 
}
```

**After:**
```kotlin
SettingsItem(
    Icons.Default.Language, 
    localizedString(LocalizationKeys.SETTINGS_LANGUAGE)
) { 
    navController.navigate("language_settings") 
}
```

Add import:
```kotlin
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString
```

### 3. Update Other Settings Items

Replace all hardcoded strings in SettingsScreen.kt:

```kotlin
// Before
Text("Other settings", ...)

// After
Text(localizedString(LocalizationKeys.SETTINGS_OTHER), ...)

// Before
SettingsItem(Icons.Default.Favorite, "Favorite Movies", ...)

// After
SettingsItem(
    Icons.Default.Favorite, 
    localizedString(LocalizationKeys.SETTINGS_FAVORITES),
    ...
)
```

---

## 📋 Migration Checklist

### Phase 1: Core Localization Setup ✅
- [x] Create `LocalizationKeys.kt` with all string constants
- [x] Create `LocalizationManager.kt` with language management
- [x] Create `LocalizationComposables.kt` with helper functions
- [x] Create `LanguageSettingsScreen.kt` for language selection

### Phase 2: Navigation Integration
- [ ] Update `MdaNavHost.kt` to include language_settings route
- [ ] Test navigation to language settings screen
- [ ] Verify back button works correctly

### Phase 3: Settings Screen Migration
- [ ] Update all hardcoded strings in `SettingsScreen.kt`
- [ ] Update `ProfileCard` strings
- [ ] Update `SettingsItem` labels
- [ ] Test settings screen in all languages

### Phase 4: Home Screen Migration
- [ ] Update greeting messages in `HomeScreen.kt`
- [ ] Update section titles (Trending, Popular, etc.)
- [ ] Update subtitle text
- [ ] Test home screen layout in all languages

### Phase 5: Search Screen Migration
- [ ] Update search placeholder in `SearchScreen.kt`
- [ ] Update filter labels
- [ ] Update "Recent Searches" title
- [ ] Update empty state message

### Phase 6: Movie Detail Screen Migration
- [ ] Update section headers in `MovieDetailScreen.kt`
- [ ] Update production details labels
- [ ] Update genre/keyword titles
- [ ] Test layout with long German text

### Phase 7: Authentication Screens
- [ ] Update `LoginScreen.kt` strings
- [ ] Update `SignupScreen.kt` strings
- [ ] Update error messages
- [ ] Test authentication flow

### Phase 8: Other Screens
- [ ] Update `FAQ.kt` strings
- [ ] Update `PrivacyPolicy.kt` strings
- [ ] Update `About.kt` strings
- [ ] Update `KidsSearchScreen.kt` strings
- [ ] Update `GenreDetailsScreen.kt` strings

### Phase 9: Testing
- [ ] Test all screens in English
- [ ] Test all screens in Arabic (RTL)
- [ ] Test all screens in German
- [ ] Test language switching
- [ ] Test persistence of language choice
- [ ] Test placeholder replacement

### Phase 10: Documentation
- [ ] Update app documentation
- [ ] Create translation guidelines
- [ ] Document any custom implementations

---

## 🎯 Implementation Examples

### Example 1: Simple String Replacement

**File**: `HomeScreen.kt`

```kotlin
// Before
LaunchedEffect(Unit) {
    while (true) {
        onTopBarStateChange(
            TopBarState(
                title = greeting,
                subtitle = "What do you want to watch?"
            )
        )
        kotlinx.coroutines.delay(5 * 60 * 1000)
    }
}

// After
LaunchedEffect(Unit) {
    while (true) {
        onTopBarStateChange(
            TopBarState(
                title = greeting,
                subtitle = localizedString(LocalizationKeys.HOME_SUBTITLE)
            )
        )
        kotlinx.coroutines.delay(5 * 60 * 1000)
    }
}
```

### Example 2: Dynamic Greeting

**File**: `HomeScreen.kt`

```kotlin
// Before
fun getGreetingMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning"
        hour < 18 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

// After
@Composable
fun getGreetingMessage(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> localizedString(LocalizationKeys.HOME_GREETING_MORNING)
        hour < 18 -> localizedString(LocalizationKeys.HOME_GREETING_AFTERNOON)
        else -> localizedString(LocalizationKeys.HOME_GREETING_EVENING)
    }
}
```

### Example 3: Error Messages with Placeholders

**File**: `SearchScreen.kt`

```kotlin
// Before
is UiState.Error -> Box(
    Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
) {
    Text(
        "Error: ${state.message}",
        color = MaterialTheme.colorScheme.error
    )
}

// After
is UiState.Error -> Box(
    Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
) {
    Text(
        localizedString(
            LocalizationKeys.SEARCH_ERROR,
            "error" to state.message
        ),
        color = MaterialTheme.colorScheme.error
    )
}
```

### Example 4: List of Localized Items

**File**: `SettingsScreen.kt`

```kotlin
// Before
SettingsItem(Icons.Default.Favorite, "Favorite Movies", onClick = { ... })
SettingsItem(Icons.Default.Person, "Actors Viewed", onClick = { ... })
SettingsItem(Icons.Default.Movie, "Movies Viewed", onClick = { ... })

// After
SettingsItem(
    Icons.Default.Favorite, 
    localizedString(LocalizationKeys.SETTINGS_FAVORITES),
    onClick = { ... }
)
SettingsItem(
    Icons.Default.Person, 
    localizedString(LocalizationKeys.SETTINGS_ACTORS_VIEWED),
    onClick = { ... }
)
SettingsItem(
    Icons.Default.Movie, 
    localizedString(LocalizationKeys.SETTINGS_MOVIES_VIEWED),
    onClick = { ... }
)
```

---

## 🧪 Testing Localization

### Unit Test Example

```kotlin
import org.junit.Test
import com.example.mda.localization.LocalizationManager
import com.example.mda.localization.LocalizationKeys

class LocalizationTest {
    
    @Test
    fun testEnglishStrings() {
        val key = LocalizationKeys.HOME_TITLE
        val en = StringsEN[key]
        assert(en != null && en.isNotEmpty())
    }
    
    @Test
    fun testArabicStrings() {
        val key = LocalizationKeys.HOME_TITLE
        val ar = StringsAR[key]
        assert(ar != null && ar.isNotEmpty())
    }
    
    @Test
    fun testGermanStrings() {
        val key = LocalizationKeys.HOME_TITLE
        val de = StringsDE[key]
        assert(de != null && de.isNotEmpty())
    }
    
    @Test
    fun testAllKeysHaveTranslations() {
        val keys = LocalizationKeys::class.java.declaredFields
            .filter { it.type == String::class.java }
            .map { it.get(null) as String }
        
        keys.forEach { key ->
            assert(StringsEN[key] != null) { "Missing EN: $key" }
            assert(StringsAR[key] != null) { "Missing AR: $key" }
            assert(StringsDE[key] != null) { "Missing DE: $key" }
        }
    }
}
```

### Manual Testing Checklist

```
[ ] Language Selection
    [ ] Can navigate to language settings
    [ ] All three languages are selectable
    [ ] Selection is saved and persists after restart

[ ] English (en)
    [ ] All text displays correctly
    [ ] No placeholder text visible
    [ ] Layout is not broken

[ ] Arabic (ar)
    [ ] All text displays correctly in RTL
    [ ] Numbers display left-to-right
    [ ] No overlapping text
    [ ] Icons are mirrored correctly

[ ] German (de)
    [ ] All text displays correctly
    [ ] Long compound words don't overflow
    [ ] Umlauts (ä, ö, ü) display correctly
    [ ] Layout is not broken

[ ] Language Switching
    [ ] Switching languages updates UI immediately
    [ ] No crashes during language switch
    [ ] All screens update correctly
    [ ] Navigation works in all languages

[ ] Edge Cases
    [ ] Very long strings don't overflow
    [ ] Placeholder replacement works correctly
    [ ] Special characters display correctly
    [ ] Empty states show localized text
```

---

## 🔍 Verification Commands

### Check for Hardcoded Strings

```bash
# Find all hardcoded strings in Kotlin files
grep -r "Text(\"" app/src/main/java/com/example/mda/ui --include="*.kt" | grep -v "localizedString"

# Find all hardcoded button labels
grep -r "Button(.*text = \"" app/src/main/java/com/example/mda/ui --include="*.kt" | grep -v "localizedString"
```

### Verify All Keys Have Translations

```bash
# Count keys in LocalizationKeys.kt
grep "const val" app/src/main/java/com/example/mda/localization/LocalizationKeys.kt | wc -l

# Count translations in each language
grep "to \"" app/src/main/java/com/example/mda/localization/LocalizationManager.kt | wc -l
```

---

## 🚀 Deployment Checklist

Before deploying:

- [ ] All screens tested in all three languages
- [ ] No hardcoded strings remain in UI code
- [ ] All localization keys are documented
- [ ] Language persistence works correctly
- [ ] RTL layout is correct for Arabic
- [ ] No crashes during language switching
- [ ] Translation quality reviewed by native speakers
- [ ] Documentation is updated
- [ ] Tests pass for all languages

---

## 📞 Common Issues & Solutions

### Issue: Text not updating after language change
```kotlin
// Solution: Make sure Composable is observing currentLanguage
val language = manager.currentLanguage.collectAsState(
    initial = LocalizationManager.Language.ENGLISH
).value
```

### Issue: Arabic text appears LTR instead of RTL
```kotlin
// Solution: Android handles this automatically
// Check device language settings if still broken
```

### Issue: Placeholder not replacing
```kotlin
// Solution: Ensure format is {placeholder}
localizedString(key, "placeholder" to value)  // Correct
localizedString(key, "placeholder", value)    // Also correct
```

### Issue: Missing translation for a key
```kotlin
// Solution: Add to all three language objects
StringsEN["key"] = "English"
StringsAR["key"] = "العربية"
StringsDE["key"] = "Deutsch"
```

---

## 📚 Additional Resources

- [Android Localization Documentation](https://developer.android.com/guide/topics/resources/localization)
- [Compose Localization Best Practices](https://developer.android.com/jetpack/compose/resources)
- [DataStore Documentation](https://developer.android.com/topic/libraries/architecture/datastore)
- [RTL Support Guide](https://developer.android.com/guide/topics/resources/multilang-support#rtl)

---

**Last Updated**: 2025
**Version**: 1.0
**Status**: Ready for Implementation
