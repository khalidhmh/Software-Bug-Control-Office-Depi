# 🌍 Localization & Internationalization Guide

## Overview
This document provides a comprehensive guide for implementing and maintaining localization in the Movie Discovery App. The app supports three languages:
- **English (en)** - Default language
- **Arabic (ar)** - Modern Standard Arabic (MSA) for universal understanding
- **German (de)** - Standard German (Hochdeutsch)

---

## 📁 Project Structure

```
app/src/main/java/com/example/mda/localization/
├── LocalizationKeys.kt          # All localization key constants
├── LocalizationManager.kt       # Language management & string retrieval
├── LocalizationComposables.kt   # Composable helpers for UI
└── LanguageSettingsScreen.kt    # Language selection UI

app/src/main/java/com/example/mda/ui/Settings/
└── LanguageSettingsScreen.kt    # Language settings screen
```

---

## 🔑 Localization Keys Naming Convention

All keys follow a consistent pattern for easy identification:

### Pattern: `CONTEXT_FEATURE_ELEMENT`

| Category | Pattern | Examples |
|----------|---------|----------|
| **Screens** | `SCREEN_ELEMENT` | `HOME_TITLE`, `SEARCH_PLACEHOLDER` |
| **Errors** | `ERROR_TYPE` | `ERROR_INVALID_EMAIL`, `ERROR_NO_INTERNET` |
| **Buttons** | `BTN_ACTION` | `BTN_LOGIN`, `BTN_SAVE`, `BTN_DELETE` |
| **Labels** | `LABEL_FIELD` | `LABEL_USERNAME`, `LABEL_EMAIL` |
| **Messages** | `MSG_TYPE` | `MSG_LOADING`, `MSG_SUCCESS`, `MSG_ERROR` |
| **Validation** | `VALIDATION_TYPE` | `VALIDATION_REQUIRED`, `VALIDATION_PASSWORD_SHORT` |
| **Navigation** | `NAV_SCREEN` | `NAV_HOME`, `NAV_SEARCH`, `NAV_FAVORITES` |
| **Common** | `COMMON_ACTION` | `COMMON_LOADING`, `COMMON_RETRY`, `COMMON_CLOSE` |

---

## 🚀 How to Use Localization

### 1. In Composable Functions

```kotlin
import com.example.mda.localization.LocalizationKeys
import com.example.mda.localization.localizedString

@Composable
fun MyScreen() {
    // Simple string
    Text(text = localizedString(LocalizationKeys.HOME_TITLE))
    
    // String with placeholder replacement
    Text(text = localizedString(
        LocalizationKeys.SEARCH_ERROR,
        "error" to "Network timeout"
    ))
}
```

### 2. Get Current Language

```kotlin
import com.example.mda.localization.LocalizationManager
import androidx.compose.runtime.collectAsState

@Composable
fun MyScreen() {
    val context = LocalContext.current
    val manager = remember { LocalizationManager(context) }
    val currentLanguage = manager.currentLanguage.collectAsState(
        initial = LocalizationManager.Language.ENGLISH
    ).value
    
    Text(text = "Current: ${currentLanguage.displayName}")
}
```

### 3. Change Language Programmatically

```kotlin
val context = LocalContext.current
val manager = remember { LocalizationManager(context) }
val scope = rememberCoroutineScope()

Button(onClick = {
    scope.launch {
        manager.setLanguage(LocalizationManager.Language.ARABIC)
    }
}) {
    Text("Switch to Arabic")
}
```

---

## 📝 Adding New Strings

### Step 1: Add Key to `LocalizationKeys.kt`

```kotlin
object LocalizationKeys {
    const val MY_NEW_STRING = "my_new_string"
}
```

### Step 2: Add Translations to All Language Objects

**In `StringsEN`:**
```kotlin
"my_new_string" to "English translation here"
```

**In `StringsAR`:**
```kotlin
"my_new_string" to "الترجمة العربية هنا"
```

**In `StringsDE`:**
```kotlin
"my_new_string" to "Deutsche Übersetzung hier"
```

### Step 3: Use in UI

```kotlin
Text(text = localizedString(LocalizationKeys.MY_NEW_STRING))
```

---

## 🌐 Language Details

### English (en)
- **Standard**: American English
- **Tone**: Professional, clear, and user-friendly
- **Usage**: Default language, used as fallback

### Arabic (ar)
- **Standard**: Modern Standard Arabic (MSA - الفصحى)
- **Tone**: Formal, professional, universally understood
- **Note**: MSA is chosen over dialects for app-wide accessibility
- **Direction**: Right-to-Left (RTL) - handled by Android system
- **Key Considerations**:
  - Avoid colloquial dialects (Egyptian, Levantine, etc.)
  - Use formal business terminology
  - Ensure clarity for all Arabic speakers

### German (de)
- **Standard**: Standard German (Hochdeutsch)
- **Tone**: Professional, formal, clear
- **Direction**: Left-to-Right (LTR)
- **Key Considerations**:
  - Use formal "Sie" for user-facing text
  - Compound words should be properly hyphenated
  - Use standard German terminology

---

## 🔄 Language Switching Flow

```
User selects language in Settings
         ↓
LanguageSettingsScreen calls manager.setLanguage()
         ↓
Language saved to DataStore
         ↓
currentLanguage Flow emits new value
         ↓
All Composables observing currentLanguage recompose
         ↓
UI updates with new language strings
```

---

## 📱 Integration with Settings Screen

The language selector is integrated into the Settings screen:

1. **Navigation**: Settings → Language
2. **Route**: `language_settings`
3. **Component**: `LanguageSettingsScreen`

### Adding to Navigation (MdaNavHost.kt):

```kotlin
composable("language_settings") {
    LanguageSettingsScreen(
        navController = navController,
        onTopBarStateChange = onTopBarStateChange
    )
}
```

---

## ✅ Translation Quality Checklist

When adding or updating translations, ensure:

- [ ] **Accuracy**: Translation conveys exact meaning, not literal
- [ ] **Consistency**: Same terms used throughout the app
- [ ] **Tone**: Matches professional, user-friendly tone
- [ ] **Length**: Fits UI constraints (no overflow)
- [ ] **Placeholders**: `{variable}` format preserved
- [ ] **Special Characters**: Properly escaped in Kotlin strings
- [ ] **RTL Support**: Arabic text displays correctly right-to-left
- [ ] **Context**: Translation makes sense in UI context

---

## 🎯 Current Localization Coverage

### Screens Covered:
- ✅ Home Screen
- ✅ Search Screen
- ✅ Movie Detail Screen
- ✅ Authentication (Login/Signup)
- ✅ Settings Screen
- ✅ Profile Screen
- ✅ Favorites Screen
- ✅ Help & FAQ
- ✅ Privacy Policy
- ✅ About Screen
- ✅ Kids Mode
- ✅ Genre Details

### String Categories:
- ✅ Navigation Titles
- ✅ Button Labels
- ✅ Form Labels
- ✅ Placeholder Text
- ✅ Error Messages
- ✅ Validation Messages
- ✅ Dialog Content
- ✅ Toast Messages
- ✅ Help Text
- ✅ Common UI Elements

---

## 🔧 Technical Implementation Details

### LocalizationManager
- **Purpose**: Central hub for language management
- **Storage**: Android DataStore (Preferences)
- **Key**: `selected_language`
- **Default**: English (en)

### Language Enum
```kotlin
enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    ARABIC("ar", "العربية"),
    GERMAN("de", "Deutsch")
}
```

### String Retrieval
- **Composable Context**: Use `localizedString()` helper
- **Non-Composable Context**: Use `manager.getString(key, language)`
- **Async Context**: Use `manager.getStringAsync(key)`

---

## 🐛 Troubleshooting

### Issue: Strings not updating after language change
**Solution**: Ensure the Composable is observing `currentLanguage` Flow

### Issue: Arabic text appears left-to-right
**Solution**: Android handles RTL automatically; check device settings

### Issue: Placeholder not replacing
**Solution**: Ensure placeholder format is `{key}` and passed correctly

### Issue: Missing translation key
**Solution**: Check all three language objects (EN, AR, DE) have the key

---

## 📚 Best Practices

1. **Always use keys**: Never hardcode strings in UI
2. **Consistent naming**: Follow the naming convention strictly
3. **Test all languages**: Verify UI layout in each language
4. **Placeholder format**: Use `{placeholder}` for dynamic content
5. **Keep translations updated**: Update all three languages together
6. **Document context**: Add comments for ambiguous strings
7. **Review translations**: Have native speakers review translations
8. **Test RTL**: Ensure Arabic layout displays correctly

---

## 🚀 Future Enhancements

- [ ] Add more languages (French, Spanish, etc.)
- [ ] Implement language auto-detection based on device settings
- [ ] Add pluralization support
- [ ] Implement date/time localization
- [ ] Add currency localization for budget/revenue
- [ ] Create translation management dashboard
- [ ] Add language-specific font support

---

## 📞 Support

For localization issues or questions:
1. Check this guide first
2. Review `LocalizationKeys.kt` for available keys
3. Check `LocalizationManager.kt` for implementation details
4. Verify translations in `StringsEN`, `StringsAR`, `StringsDE`

---

**Last Updated**: 2025
**Version**: 1.0
**Status**: Production Ready
