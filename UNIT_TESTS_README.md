# Unit Tests Documentation

## Overview
This document describes the unit tests for language settings, password screens, and kids mode functionality.

## Test Files Created

### 1. LocalizationManagerTest.kt
**Location:** `app/src/test/java/com/example/mda/localization/LocalizationManagerTest.kt`

**Tests:**
- Default language initialization (English)
- Language switching (Arabic, German)
- Language persistence across app restarts
- String retrieval for all supported languages
- Password-related localization keys
- Language settings keys
- Security questions keys
- Language code mapping
- Display name validation

**Key Test Cases:**
```kotlin
testDefaultLanguageIsEnglish()
testLanguageSwitchToArabic()
testLanguagePersistence()
testGetStringEnglish()
testPasswordSettingsKeysExist()
```

### 2. KidsSecurityDataStoreTest.kt
**Location:** `app/src/test/java/com/example/mda/data/datastore/KidsSecurityDataStoreTest.kt`

**Tests:**
- PIN storage and retrieval
- PIN clearing
- Lock enable/disable
- Kids mode active/inactive state
- Security questions storage
- Data persistence across app restarts
- PIN trimming on save
- Multiple PIN changes
- Default values

**Key Test Cases:**
```kotlin
testSetAndGetPin()
testClearPin()
testSetLockEnabled()
testSetActive()
testPinPersistence()
testActivePersistence()
testSecurityQAPersistence()
```

### 3. PinValidationTest.kt
**Location:** `app/src/test/java/com/example/mda/validation/PinValidationTest.kt`

**Tests:**
- PIN length validation (4 and 6 digits)
- PIN comparison
- PIN comparison with whitespace
- Empty PIN validation
- PIN with special characters
- Required PIN length calculation
- PIN mismatch detection
- PIN match detection
- NULL PIN handling

**Key Test Cases:**
```kotlin
testValidPinLength4()
testValidPinLength6()
testPinComparison()
testPinComparisonWithWhitespace()
testRequiredPinLength()
testPinMismatchDetection()
```

### 4. KidsModeTest.kt
**Location:** `app/src/test/java/com/example/mda/kids/KidsModeTest.kt`

**Tests:**
- Kids mode activation/deactivation
- Kids mode persistence
- Kids mode with lock enabled
- Kids mode with lock disabled
- Exit with correct PIN
- Exit with incorrect PIN
- Exit without lock
- Kids mode with security questions
- Kids mode toggle
- Legacy PIN support (4-digit)
- Modern PIN support (6-digit)
- Complete kids mode flow

**Key Test Cases:**
```kotlin
testKidsModeActivation()
testKidsModeDeactivation()
testKidsModeExitWithCorrectPin()
testKidsModeExitWithIncorrectPin()
testKidsModeCompleteFlow()
```

### 5. LocalizationKeysTest.kt
**Location:** `app/src/test/java/com/example/mda/localization/LocalizationKeysTest.kt`

**Tests:**
- Password settings keys existence
- PIN input keys existence
- PIN hint keys existence
- PIN error keys existence
- Security questions keys existence
- Language settings keys existence
- Common keys existence
- Settings keys existence
- Key naming convention
- Key count validation
- No key duplication

**Key Test Cases:**
```kotlin
testPasswordSettingsKeysExist()
testSecurityQuestionsKeysExist()
testLanguageSettingsKeysExist()
testNoKeyDuplication()
```

## Running Tests

### Run All Unit Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests com.example.mda.localization.LocalizationManagerTest
./gradlew test --tests com.example.mda.data.datastore.KidsSecurityDataStoreTest
./gradlew test --tests com.example.mda.validation.PinValidationTest
./gradlew test --tests com.example.mda.kids.KidsModeTest
./gradlew test --tests com.example.mda.localization.LocalizationKeysTest
```

### Run Specific Test Method
```bash
./gradlew test --tests com.example.mda.localization.LocalizationManagerTest.testDefaultLanguageIsEnglish
```

### Run Tests with Detailed Output
```bash
./gradlew test --info
```

### Run Tests and Generate Report
```bash
./gradlew test
# Report will be generated at: app/build/reports/tests/test/index.html
```

## Test Coverage

### Localization (LocalizationManagerTest)
- ✅ Language switching
- ✅ Language persistence
- ✅ Translation retrieval
- ✅ All supported languages (EN, AR, DE)
- ✅ Key existence validation

### Password Management (KidsSecurityDataStoreTest)
- ✅ PIN storage
- ✅ PIN retrieval
- ✅ PIN clearing
- ✅ Lock state management
- ✅ Data persistence

### PIN Validation (PinValidationTest)
- ✅ Length validation
- ✅ Format validation
- ✅ Comparison logic
- ✅ Whitespace handling
- ✅ Edge cases

### Kids Mode (KidsModeTest)
- ✅ Mode activation/deactivation
- ✅ State persistence
- ✅ PIN-based exit
- ✅ Lock management
- ✅ Complete flow

### Localization Keys (LocalizationKeysTest)
- ✅ Key existence
- ✅ Key naming convention
- ✅ No duplication
- ✅ Complete coverage

## Expected Test Results

All tests should pass with the following summary:
```
LocalizationManagerTest: 10 tests passed
KidsSecurityDataStoreTest: 15 tests passed
PinValidationTest: 15 tests passed
KidsModeTest: 14 tests passed
LocalizationKeysTest: 12 tests passed

Total: 66 tests passed
```

## Troubleshooting

### Tests Fail with "Context is null"
- Ensure you're using `ApplicationProvider.getApplicationContext()` in `@Before` method
- Add `@RunWith(AndroidJUnit4::class)` for Android-specific tests

### Tests Fail with "Flow timeout"
- Ensure you're using `runBlocking { }` for suspend functions
- Use `.first()` to get the first value from Flow

### Tests Fail with "Key not found"
- Verify LocalizationKeys are properly defined
- Check LocalizationManager has translations for all keys

## CI/CD Integration

To integrate these tests in CI/CD:

```yaml
# Example GitHub Actions
- name: Run Unit Tests
  run: ./gradlew test
  
- name: Upload Test Reports
  uses: actions/upload-artifact@v2
  if: always()
  with:
    name: test-reports
    path: app/build/reports/tests/
```

## Best Practices

1. **Isolation:** Each test is independent and doesn't affect others
2. **Clarity:** Test names clearly describe what is being tested
3. **Coverage:** Tests cover happy paths, edge cases, and error scenarios
4. **Performance:** Tests run quickly (< 5 seconds total)
5. **Maintainability:** Tests are easy to understand and modify

## Future Enhancements

- [ ] Add instrumented tests for UI components
- [ ] Add integration tests for complete flows
- [ ] Add performance benchmarks
- [ ] Add code coverage reports
- [ ] Add mutation testing

## Contact

For questions or issues with tests, please refer to the main project documentation.
