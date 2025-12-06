package com.example.mda.localization

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.localizationDataStore: DataStore<Preferences> by preferencesDataStore(name = "localization_prefs")

class LocalizationManager(private val context: Context) {
    private val LANGUAGE_KEY = stringPreferencesKey("selected_language")

    // Available languages
    enum class Language(val code: String, val displayName: String) {
        ENGLISH("en", "English"),
        ARABIC("ar", "العربية"),
        GERMAN("de", "Deutsch")
    }

    // Get current language as Flow
    val currentLanguage: Flow<Language> = context.localizationDataStore.data
        .map { preferences ->
            val code = preferences[LANGUAGE_KEY] ?: "en"
            Language.values().find { it.code == code } ?: Language.ENGLISH
        }

    // Set language
    suspend fun setLanguage(language: Language) {
        context.localizationDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.code
        }
        // Update global language provider for network layer
        LanguageProvider.currentCode = language.code
    }

     // Get string by key and language with English fallback if missing
    fun getString(key: String, language: Language): String {
        val value = when (language) {
            Language.ENGLISH -> StringsEN[key]
            Language.ARABIC -> StringsAR[key]
            Language.GERMAN -> StringsDE[key]
        }
        if (value != null) return value
        // Fallback to English if translation missing
        val en = StringsEN[key]
        if (en != null) return en
        // As a last resort, return the key (useful to detect missing entries during dev)
        return key
    }

    // Get string by key using current language (requires coroutine context)
    suspend fun getStringAsync(key: String): String {
        val lang = currentLanguage.first()
        return getString(key, lang)
    }
}

// ==================== ENGLISH STRINGS ====================
object StringsEN {
    private val strings = mapOf(
        // Home Screen
        "home_greeting_morning" to "Good Morning",
        "home_greeting_afternoon" to "Good Afternoon",
        "home_greeting_evening" to "Good Evening",
        "home_greeting_night" to "Good Night",
        "home_subtitle" to "What do you want to watch?",
        "home_banner_title" to "Featured",
        "home_for_you_title" to "For You",
        "home_trending_title" to "Trending",
        "home_trending_today" to "Today",
        "home_trending_week" to "This Week",
        "home_popular_movies" to "Popular Movies",
        "home_popular_tv" to "Popular TV Shows",
        "home_top_rated" to "Top Rated",

        // Search Screen
        "search_title" to "Search",
        "search_filter_all" to "All",
        "search_placeholder" to "Search movies, shows, people...",
        "search_filter_movies" to "Movies",
        "search_filter_tv" to "TV Shows",
        "search_filter_people" to "People",
        "search_recent_title" to "Recent Searches",
        "search_clear_all" to "Clear all",
        "search_no_results" to "No results found for",
        "search_try_another" to "Try searching for another movie or person ",
        "search_start_typing" to "Start typing to search ",
        "search_error" to "Error: {error}",

        // Movie Detail Screen
        "detail_production_details" to "Production Details",
        "detail_spoken_languages" to "Spoken Languages",
        "detail_production_countries" to "Production Countries",
        "detail_companies" to "Companies",
        "detail_genres" to "Genres",
        "detail_keywords" to "Keywords",
        "detail_cast" to "Cast",
        "detail_reviews" to "Reviews",
        "detail_videos" to "Videos",
        "detail_runtime" to "Runtime",
        "detail_release_date" to "Release Date",
        "detail_budget" to "Budget",
        "detail_revenue" to "Revenue",
        "detail_rating" to "Rating",
        "detail_overview" to "Overview",
        "detail_discover" to "Discover",
        "detail_recommendations_count" to "Recommendations {count}",
        "detail_similar_count" to "Similar {count}",
        "detail_no_recommendations" to "No recommendations",
        "detail_no_similar" to "No similar",

        // Detail About extras
        "detail_about_movie" to "About movie",
        "detail_status" to "Status",
        "detail_original_language" to "Original language",
        "detail_production_country" to "Production country",
        "detail_production_companies" to "Production companies",

        // Authentication
        "auth_login_title" to "Login with TMDb",
        "auth_signup_title" to "Sign Up",
        "auth_authenticating" to "Authenticating...",
        "auth_error" to "Error: {error}",
        "auth_error_approve" to "Please make sure you approved the authentication in your browser.",
        "auth_retry" to "Retry",
        "auth_login_required" to "Login Required",
        "auth_login_required_msg" to "You must login to add this movie to favorites.",

        // Settings Screen
        "settings_title" to "Settings",
        "settings_other" to "Other settings",
        "settings_favorites" to "Favorite Movies",
        "settings_actors_viewed" to "Actors Viewed",
        "settings_movies_viewed" to "Movies Viewed",
        "settings_password" to "Password",
        "settings_notifications" to "Notifications",
        "settings_dark_mode" to "Dark Mode",
        "settings_language" to "Language",
        "settings_kids_mode" to "Kids Mode",
        "settings_privacy_policy" to "Privacy Policy",
        "settings_help_faq" to "Help / FAQ",
        "settings_about" to "About",
        "settings_login_prompt" to "Login or Sign up",
        "settings_login_subtitle" to "Access your account to sync settings",
        // Language Settings Screen
        "settings_language_select_title" to "Select Your Language",
        "settings_language_info_title" to "Language Information",
        "settings_language_info_body" to "Your language preference will be applied immediately across the entire app. All content will be displayed in your selected language.",
        "settings_language_selected_cd" to "Selected",

        // Profile
        "profile_title" to "Profile",
        "profile_login_or_signup" to "Login or Sign up",
        "profile_access_account" to "Access your account to sync settings",

        // Favorites
        "favorites_title" to "Favorites",
        "favorites_empty" to "No favorites yet",
        "favorites_add_success" to "Added to favorites",
        "favorites_remove_success" to "Removed from favorites",

        // Help & FAQ
        "help_title" to "Help & FAQ",
        "help_faq_q1" to "How does the movie search work?",
        "help_faq_a1" to "You can search for any movie by typing its title in the search bar. The results update instantly as you type.",
        "help_faq_q2" to "How can I save movies to favorites?",
        "help_faq_a2" to "Open any movie and tap the heart icon to add it to your favorites list.",
        "help_faq_q3" to "Are my favorites synced across devices?",
        "help_faq_a3" to "Yes, once you sign in using TMDb, your favorites are synced using Firebase.",
        "help_faq_q4" to "Why do I need to log in?",
        "help_faq_a4" to "Logging in allows you to sync favorites and personalize your experience safely.",
        "help_faq_q5" to "Why are some movies not appearing?",
        "help_faq_a5" to "This may happen if the movie is not available in TMDb or due to network issues.",
        "help_faq_q6" to "Data loading is slow. What can I do?",
        "help_faq_a6" to "Check your internet connection or swipe down to refresh the page.",

        // Privacy Policy
        "privacy_title" to "Privacy Policy",
        "privacy_commitment_title" to "Our Privacy Commitment",
        "privacy_commitment_desc" to "We value your privacy and handle your data responsibly. Our app uses The Movie Database (TMDb) API for movie data and account integration only.",
        "privacy_data_title" to "What Data We Use",
        "privacy_data_desc" to "We do not collect or store any of your personal data locally. All data such as favorites or account info come directly from your TMDb account.",
        "privacy_management_title" to "How Your Data is Managed",
        "privacy_management_desc" to "Your account interactions are handled securely via TMDb. We do not share or sell your data to any third parties.",
        "privacy_learn_more_title" to "Learn More",
        "privacy_learn_more_desc" to "Click the section below to visit TMDb's official privacy policy for full details.",
        "privacy_view_tmdb" to "View TMDb Privacy Policy",

        // About
        "about_title" to "About",
        "about_app_name" to "Movie Discovery App",
        "about_app_desc" to "Discover trending, popular & upcoming movies effortlessly.",
        "about_objectives" to "Objectives",
        "about_obj_1" to "Explore trending, popular, and upcoming movies.",
        "about_obj_2" to "Integrate TMDB API for real-time data.",
        "about_obj_3" to "Smart search for fast & relevant results.",
        "about_obj_4" to "Save & sync favorites with Room & Firebase.",
        "about_obj_5" to "Elegant UI & smooth UX with Jetpack Compose and Material 3.",
        "about_team" to "Team SBCO",
        "about_team_member_1" to "Khalid Hussien – Team Leader / Developer",
        "about_team_member_2" to "Saleh Mohamed – UI/UX Designer / Developer",
        "about_team_member_3" to "Fares Diaa – Developer",
        "about_team_member_4" to "Omar Atallah – Developer",
        "about_team_member_5" to "Sara Essam – Developer",
        "about_tech_title" to "Technologies Used",
        "about_tech_item_1" to "Kotlin, Jetpack Compose, MVVM",
        "about_tech_item_2" to "Room & Firebase Realtime Database",
        "about_tech_item_3" to "Retrofit + Coroutines",
        "about_tech_item_4" to "Navigation Component",
        "about_tech_item_5" to "Figma, Coil, Material 3",
        "about_tech_item_6" to "Git & GitHub, Gradle, Android Studio",
        "about_copyright" to "© 2025 SBCO – All Rights Reserved",
        "about_technologies" to "Technologies Used",
        "about_tech_1" to "Kotlin, Jetpack Compose, MVVM",
        "about_tech_2" to "Room & Firebase Realtime Database",
        "about_tech_3" to "Retrofit + Coroutines",
        "about_tech_4" to "Navigation Component",
        "about_tech_5" to "Figma, Coil, Material 3",
        "about_tech_6" to "Git & GitHub, Gradle, Android Studio",
        "about_copyright" to "© 2025 SBCO – All Rights Reserved",
         "developer_tools" to "Developer Tools",

        // Kids Mode
        "kids_title" to "Kids Mode",
        "kids_search_placeholder" to "Search kids-safe content...",
        "kids_filter_movies" to "Movies",
        "kids_filter_tv" to "TV Shows",
        "kids_recent_searches" to "Recent Searches",
        "kids_try_another" to "Try searching for another kids movie or TV show",
        "kids_no_results" to "No results found for",
        "kids_clear_all" to "Clear all",


        // Genre Details
        "genre_title" to "{genre}",
        "genre_filter_all" to "All Movies",
        "genre_filter_top_rated" to "Top Rated",
        "genre_filter_newest" to "Newest",
        "genre_filter_popular" to "Most Popular",
        "genre_filter_family" to "Family Friendly",
        "genre_filter_dialog_title" to "Filter Movies",
        "genre_filter_close" to "Close",

        // Buttons
        "btn_login" to "Login",
        "btn_signup" to "Sign Up",
        "btn_logout" to "Logout",
        "btn_cancel" to "Cancel",
        "btn_save" to "Save",
        "btn_delete" to "Delete",
        "btn_retry" to "Retry",
        "btn_close" to "Close",
        "btn_confirm" to "Confirm",
        "btn_back" to "Back",
        "btn_next" to "Next",
        "btn_previous" to "Previous",

        // Labels
        "label_username" to "Username",
        "label_email" to "Email",
        "label_password" to "Password",
        "label_confirm_password" to "Confirm Password",
        "label_language" to "Language",
        "label_theme" to "Theme",

        // Messages
        "msg_loading" to "Loading...",
        "msg_success" to "Success",
        "msg_error" to "Error",
        "msg_empty" to "No data available",
        "msg_no_internet" to "No internet connection",
        "msg_refresh" to "Refresh",

        // Validation
        "validation_required" to "This field is required",
        "validation_invalid_email" to "Invalid email address",
        "validation_password_short" to "Password must be at least 6 characters",
        "validation_password_mismatch" to "Passwords do not match",

        // Dialogs
        "dialog_confirm_delete" to "Are you sure you want to delete this?",
        "dialog_confirm_logout" to "Are you sure you want to logout?",
        "dialog_confirm_clear_history" to "Are you sure you want to clear your search history?",

        // Navigation
        "nav_home" to "Home",
        "nav_movies" to "Movies",
        "nav_actors" to "Actors",
        "nav_search" to "Search",
        "nav_favorites" to "Favorites",
        "nav_settings" to "Settings",
        "nav_profile" to "Profile",
        "nav_kids" to "Kids",
        "nav_history" to "History",
        "nav_tv" to "TV",

        // Genres
        "genre_action" to "Action",
        "genre_adventure" to "Adventure",
        "genre_animation" to "Animation",
        "genre_comedy" to "Comedy",
        "genre_crime" to "Crime",
        "genre_documentary" to "Documentary",
        "genre_drama" to "Drama",
        "genre_family" to "Family",
        "genre_fantasy" to "Fantasy",
        "genre_history" to "History",
        "genre_horror" to "Horror",
        "genre_music" to "Music",
        "genre_mystery" to "Mystery",
        "genre_romance" to "Romance",
        "genre_science_fiction" to "Science Fiction",
        "genre_tv_movie" to "TV Movie",
        "genre_thriller" to "Thriller",
        "genre_war" to "War",
        "genre_western" to "Western",

        // Filter Options
        "filter_all_movies" to "All Movies",
        "filter_top_rated" to "Top Rated",
        "filter_newest" to "Newest",
        "filter_most_popular" to "Most Popular",
        "filter_family_friendly" to "Family Friendly",

        // Common
        "common_loading" to "Loading...",
        "common_error" to "Error",
        "common_retry" to "Retry",
        "common_close" to "Close",
        "common_back" to "Back",
        "common_next" to "Next",
        "common_previous" to "Previous",
        "common_cancel" to "Cancel",
        "common_confirm" to "Confirm",
        "common_delete" to "Delete",
        "common_save" to "Save",
        "common_edit" to "Edit",
        "common_share" to "Share",
        "common_copy" to "Copy",
        "common_no_image" to "No image",
        "common_show_more" to "Show more",
        "common_show_less" to "Show less",
        "common_expand" to "Expand",
        "common_collapse" to "Collapse",

        // -------------------- Password / Kids PIN --------------------
        "pw_manage_desc" to "Manage Kids Mode password and lock settings.",
        "pw_require_pin_label" to "Require PIN to exit Kids mode",
        "pw_set_pin_first" to "Set a PIN first",
        "pw_set_kids_pin" to "Set Kids PIN",
        "pw_change_kids_pin" to "Change Kids PIN",
        "pw_clear_kids_pin" to "Clear Kids PIN",
        "pw_clear_dialog_title" to "Enter PIN to clear",
        "pw_clear_dialog_desc" to "Clearing will remove the PIN and disable lock.",
        "pw_incorrect_pin" to "Incorrect PIN",
        "pw_hint_set" to "Hint: Create a 6-digit PIN. You'll confirm it on the next step.",
        "pw_enter_pin" to "Enter 6-digit PIN",
        "pw_confirm_pin" to "Confirm PIN",
        "pw_pins_mismatch" to "PINs do not match",
        "pw_pins_mismatch_try_again" to "PINs do not match. Try again.",
        "pw_hint_change" to "Hint: Enter old PIN, then set and confirm a new 6-digit PIN.",
        "pw_enter_old_pin" to "Enter old PIN",
        "pw_enter_new_pin" to "Enter new 6-digit PIN",
        "pw_confirm_new_pin" to "Confirm new PIN",
        "pw_incorrect_old_pin" to "Incorrect old PIN",
        "pw_forgot_pin" to "Forgot PIN?",

        // -------------------- Security Questions --------------------
        "sq_title" to "Security Questions",
        "sq_verify_title" to "Verify Identity",
        "sq_header_setup" to "Answer recovery questions (choose from lists)",
        "sq_subtext_setup" to "Please select a question and an answer for each. Placeholders are not allowed.",
        "sq_header_verify" to "Answer your security questions to continue",
        "sq_hint_verify" to "Hint: Choose the correct answers you picked before. Order is randomized.",
        "sq_not_set" to "Security questions are not set.",
        "sq_select_all_questions" to "Please select all three questions",
        "sq_select_answer_each" to "Please select an answer for each question",
        "sq_questions_must_differ" to "Questions must be different",
        "sq_please_select_every_answer" to "Please select an answer for every question",
        "sq_answers_not_match" to "Answers do not match",
        "sq_btn_verify" to "Verify",
        "sq_dialog_reset_title" to "Reset security questions?",
        "sq_dialog_reset_text" to "Do you want to update your recovery questions before setting a new PIN?",
        "sq_dialog_yes_update" to "Yes, update",
        "sq_dialog_no_continue" to "No, continue",
        "sq_question_1" to "Question 1",
        "sq_question_2" to "Question 2",
        "sq_question_3" to "Question 3"
    )

    operator fun get(key: String): String? = strings[key]
}

// ==================== ARABIC STRINGS ====================
object StringsAR {
    private val strings = mapOf(
        // Home Screen
        "home_greeting_morning" to "صباح الخير",
        "home_greeting_afternoon" to "مساء الخير",
        "home_greeting_evening" to "تصبح على خير",
        "home_greeting_night" to "تصبح على خير",
        "home_subtitle" to "ماذا تريد أن تشاهد؟",
        "home_banner_title" to "المختارة",
        "home_for_you_title" to "من أجلك",
        "home_trending_title" to "الرائج الآن",
        "home_trending_today" to "اليوم",
        "home_trending_week" to "هذا الأسبوع",
        "home_popular_movies" to "الأفلام الشهيرة",
        "home_popular_tv" to "المسلسلات الشهيرة",
        "home_top_rated" to "الأعلى تقييماً",
        "developer_tools" to "أدوات المطورين",
        // Search Screen
        "search_title" to "البحث",
        "search_filter_all" to "الكل",
        "search_placeholder" to "ابحث عن أفلام أو مسلسلات أو ممثلين...",
        "search_filter_movies" to "أفلام",
        "search_filter_tv" to "مسلسلات",
        "search_filter_people" to "ممثلون",
        "search_recent_title" to "عمليات البحث الأخيرة",
        "search_clear_all" to "حذف الكل",
        "search_no_results" to "لم يتم العثور على نتائج",
        "search_try_another" to "جرّب البحث عن فيلم أو مسلسل آخر",
        "search_error" to "خطأ: {error}",
        // -------------------- Kids Mode --------------------
        "kids_title" to "وضع الأطفال",
        "kids_search_placeholder" to "ابحث عن محتوى آمن للأطفال...",
        "kids_filter_all" to "الكل",
        "kids_filter_movies" to "أفلام",
        "kids_filter_tv" to "مسلسلات",
        "kids_recent_searches" to "عمليات البحث الأخيرة",
        "kids_no_results" to "لم يتم العثور على نتائج لـ",
        "kids_try_another" to "جرّب البحث عن فيلم أطفال آخر ",
        "kids_clear_all" to "مسح الكل",

        // -------------------- Password / Kids PIN --------------------
        "pw_manage_desc" to "إدارة كلمة مرور وضع الأطفال وإعدادات القفل.",
        "pw_require_pin_label" to "طلب رمز PIN للخروج من وضع الأطفال",
        "pw_set_pin_first" to "قم بتعيين رمز PIN أولاً",
        "pw_set_kids_pin" to "تعيين رمز PIN للأطفال",
        "pw_change_kids_pin" to "تغيير رمز PIN للأطفال",
        "pw_clear_kids_pin" to "مسح رمز PIN للأطفال",
        "pw_clear_dialog_title" to "أدخل رمز PIN للمسح",
        "pw_clear_dialog_desc" to "سيؤدي المسح إلى إزالة الرمز وتعطيل القفل.",
        "pw_incorrect_pin" to "رمز PIN غير صحيح",
        "pw_hint_set" to "تلميح: أنشئ رمز PIN مكون من 6 أرقام. ستقوم بتأكيده في الخطوة التالية.",
        "pw_enter_pin" to "أدخل رمز PIN من 6 أرقام",
        "pw_confirm_pin" to "تأكيد الرمز",
        "pw_pins_mismatch" to "الرمزان غير متطابقين",
        "pw_pins_mismatch_try_again" to "الرمزان غير متطابقين. حاول مرة أخرى.",
        "pw_hint_change" to "تلميح: أدخل الرمز القديم، ثم أدخل رمزًا جديدًا من 6 أرقام وأكده.",
        "pw_enter_old_pin" to "أدخل الرمز القديم",
        "pw_enter_new_pin" to "أدخل رمزًا جديدًا من 6 أرقام",
        "pw_confirm_new_pin" to "تأكيد الرمز الجديد",
        "pw_incorrect_old_pin" to "الرمز القديم غير صحيح",
        "pw_forgot_pin" to "نسيت الرمز؟",

        // -------------------- Security Questions --------------------
        "sq_title" to "أسئلة الأمان",
        "sq_verify_title" to "تحقق من الهوية",
        "sq_header_setup" to "أجب عن أسئلة الاستعادة (اختر من القوائم)",
        "sq_subtext_setup" to "يرجى اختيار سؤال وإجابة لكل منهما. لا يُسمح بالقيم الافتراضية.",
        "sq_header_verify" to "أجب عن أسئلة الأمان للمتابعة",
        "sq_hint_verify" to "تلميح: اختر الإجابات الصحيحة التي اخترتها سابقًا. الترتيب عشوائي.",
        "sq_not_set" to "لم يتم إعداد أسئلة الأمان.",
        "sq_select_all_questions" to "يرجى اختيار الأسئلة الثلاثة",
        "sq_select_answer_each" to "يرجى اختيار إجابة لكل سؤال",
        "sq_questions_must_differ" to "يجب أن تكون الأسئلة مختلفة",
        "sq_please_select_every_answer" to "يرجى اختيار إجابة لكل سؤال",
        "sq_answers_not_match" to "الإجابات غير متطابقة",
        "sq_btn_verify" to "تحقق",
        "sq_dialog_reset_title" to "هل تريد إعادة تعيين أسئلة الأمان؟",
        "sq_dialog_reset_text" to "هل تريد تحديث أسئلة الاستعادة قبل تعيين رمز PIN جديد؟",
        "sq_dialog_yes_update" to "نعم، تحديث",
        "sq_dialog_no_continue" to "لا، متابعة",
        "sq_question_1" to "السؤال 1",
        "sq_question_2" to "السؤال 2",
        "sq_question_3" to "السؤال 3",


        // Movie Detail Screen
        "detail_production_details" to "تفاصيل الإنتاج",
        "detail_spoken_languages" to "اللغات المستخدمة",
        "detail_production_countries" to "دول الإنتاج",
        "detail_companies" to "شركات الإنتاج",
        "detail_genres" to "الأنواع",
        "detail_keywords" to "الكلمات المفتاحية",
        "detail_cast" to "Cast",
        "detail_reviews" to "التقييمات",
        "detail_videos" to "الفيديوهات",
        "detail_runtime" to "المدة الزمنية",
        "detail_release_date" to "تاريخ الإصدار",
        "detail_budget" to "الميزانية",
        "detail_revenue" to "الإيرادات",
        "detail_rating" to "التقييم",
        "detail_overview" to "نظرة عامة",
        "detail_discover" to "اكتشف",
        "detail_recommendations_count" to "التوصيات {count}",
        "detail_similar_count" to "مشابهة {count}",
        "detail_no_recommendations" to "لا توجد توصيات",
        "detail_no_similar" to "لا توجد أعمال مشابهة",

        // Detail About extras
        "detail_about_movie" to "عن الفيلم",
        "detail_status" to "الحالة",
        "detail_original_language" to "اللغة الأصلية",
        "detail_production_country" to "بلد الإنتاج",
        "detail_production_companies" to "شركات الإنتاج",

        // Authentication
        "auth_login_title" to "تسجيل الدخول عبر TMDb",
        "auth_signup_title" to "إنشاء حساب",
        "auth_authenticating" to "جاري المصادقة...",
        "auth_error" to "خطأ: {error}",
        "auth_error_approve" to "يرجى التأكد من الموافقة على المصادقة في متصفحك.",
        "auth_retry" to "إعادة محاولة",
        "auth_login_required" to "تسجيل الدخول مطلوب",
        "auth_login_required_msg" to "يجب عليك تسجيل الدخول لإضافة هذا الفيلم إلى المفضلة.",

        // Settings Screen
        "settings_title" to "الإعدادات",
        "settings_other" to "إعدادات أخرى",
        "settings_favorites" to "الأفلام المفضلة",
        "settings_actors_viewed" to "سجل الممثلين",
        "settings_movies_viewed" to "الأفلام المشاهدة",
        "settings_password" to "كلمة المرور",
        "settings_notifications" to "الإشعارات",
        "settings_dark_mode" to "الوضع الداكن",
        "settings_language" to "اللغة",
        "settings_kids_mode" to "وضع الأطفال",
        "settings_privacy_policy" to "سياسة الخصوصية",
        "settings_help_faq" to "المساعدة والأسئلة الشائعة",
        "settings_about" to "حول التطبيق",
        "settings_login_prompt" to "تسجيل الدخول أو إنشاء حساب",
        "settings_login_subtitle" to "الوصول إلى حسابك لمزامنة الإعدادات",
        // Language Settings Screen
        "settings_language_select_title" to "اختر لغتك",
        "settings_language_info_title" to "معلومات اللغة",
        "settings_language_info_body" to "سيتم تطبيق تفضيل اللغة الخاص بك فورًا عبر التطبيق بالكامل. سيتم عرض جميع المحتويات باللغة التي اخترتها.",
        "settings_language_selected_cd" to "محدد",

        // Profile
        "profile_title" to "الملف الشخصي",
        "profile_login_or_signup" to "تسجيل الدخول أو إنشاء حساب",
        "profile_access_account" to "الوصول إلى حسابك لمزامنة الإعدادات",

        // Favorites
        "favorites_title" to "المفضلة",
        "favorites_empty" to "لا توجد أفلام مفضلة حتى الآن",
        "favorites_add_success" to "تمت الإضافة إلى المفضلة",
        "favorites_remove_success" to "تمت الإزالة من المفضلة",

        // Navigation
        "nav_home" to "الرئيسية",
        "nav_movies" to "الأفلام",
        "nav_actors" to "الممثلون",
        "nav_search" to "البحث",
        "nav_favorites" to "المفضلة",
        "nav_settings" to "الإعدادات",
        "nav_profile" to "الملف الشخصي",
        "nav_kids" to "الأطفال",
        "nav_history" to "السجل",
        "nav_tv" to "التلفاز",

        // Genres
        "genre_action" to "أكشن",
        "genre_adventure" to "مغامرة",
        "genre_animation" to "رسوم متحركة",
        "genre_comedy" to "كوميديا",
        "genre_crime" to "جريمة",
        "genre_documentary" to "وثائقي",
        "genre_drama" to "دراما",
        "genre_family" to "عائلي",
        "genre_fantasy" to "فانتازيا",
        "genre_history" to "تاريخي",
        "genre_horror" to "رعب",
        "genre_music" to "موسيقى",
        "genre_mystery" to "غموض",
        "genre_romance" to "رومانسي",
        "genre_science_fiction" to "خيال علمي",
        "genre_tv_movie" to "فيلم تلفزيوني",
        "genre_thriller" to "إثارة",
        "genre_war" to "حرب",
        "genre_western" to "ويسترن",

        // Help & FAQ
        "help_title" to "المساعدة والأسئلة الشائعة",
        "help_faq_q1" to "كيف يعمل البحث عن الأفلام؟",
        "help_faq_a1" to "يمكنك البحث عن أي فيلم بكتابة اسمه في شريط البحث. تتحدث النتائج على الفور أثناء الكتابة.",
        "help_faq_q2" to "كيف يمكنني حفظ الأفلام في المفضلة؟",
        "help_faq_a2" to "افتح أي فيلم واضغط على أيقونة القلب لإضافته إلى قائمة المفضلة.",
        "help_faq_q3" to "هل يتم مزامنة المفضلة الخاصة بي عبر الأجهزة؟",
        "help_faq_a3" to "نعم، بمجرد تسجيل الدخول باستخدام TMDb، يتم مزامنة المفضلة الخاصة بك باستخدام Firebase.",
        "help_faq_q4" to "لماذا أحتاج إلى تسجيل الدخول؟",
        "help_faq_a4" to "يسمح لك تسجيل الدخول بمزامنة المفضلة وتخصيص تجربتك بأمان.",
        "help_faq_q5" to "لماذا لا تظهر بعض الأفلام؟",
        "help_faq_a5" to "قد يحدث هذا إذا لم يكن الفيلم متاحاً في TMDb أو بسبب مشاكل في الشبكة.",
        "help_faq_q6" to "تحميل البيانات بطيء. ماذا يمكنني أن أفعل؟",
        "help_faq_a6" to "تحقق من اتصالك بالإنترنت أو اسحب لأسفل لتحديث الصفحة.",

        // Privacy Policy
        "privacy_title" to "سياسة الخصوصية",
        "privacy_commitment_title" to "التزامنا بالخصوصية",
        "privacy_commitment_desc" to "نحن نقدر خصوصيتك ونتعامل مع بياناتك بمسؤولية. يستخدم تطبيقنا واجهة برمجة تطبيقات The Movie Database (TMDb) لبيانات الأفلام والتكامل مع الحساب فقط.",
        "privacy_data_title" to "البيانات التي نستخدمها",
        "privacy_data_desc" to "لا نجمع أو نخزن أي بيانات شخصية لديك محلياً. جميع البيانات مثل المفضلة أو معلومات الحساب تأتي مباشرة من حسابك على TMDb.",
        "privacy_management_title" to "كيفية إدارة بياناتك",
        "privacy_management_desc" to "يتم التعامل مع تفاعلات حسابك بأمان عبر TMDb. لا نشارك أو نبيع بياناتك لأي جهات خارجية.",
        "privacy_learn_more_title" to "اعرف المزيد",
        "privacy_learn_more_desc" to "انقر على القسم أدناه لزيارة سياسة الخصوصية الرسمية لـ TMDb للحصول على التفاصيل الكاملة.",
        "privacy_view_tmdb" to "عرض سياسة خصوصية TMDb",

        // About
        "about_title" to "حول التطبيق",
        "about_app_name" to "تطبيق اكتشاف الأفلام",
        "about_app_desc" to "اكتشف الأفلام الرائجة والشهيرة والقادمة بسهولة.",
        "about_objectives" to "الأهداف",
        "about_obj_1" to "استكشف الأفلام الرائجة والشهيرة والقادمة.",
        "about_obj_2" to "دمج واجهة برمجة تطبيقات TMDB للحصول على بيانات فورية.",
        "about_obj_3" to "بحث ذكي للحصول على نتائج سريعة وملائمة.",
        "about_obj_4" to "حفظ ومزامنة المفضلة مع Room و Firebase.",
        "about_obj_5" to "واجهة أنيقة وتجربة سلسة مع Jetpack Compose و Material 3.",
        "about_team" to "فريق SBCO",
        "about_team_member_1" to "خالد حسين – قائد الفريق / مطور",
        "about_team_member_2" to "صالح محمد – مصمم واجهات وتجربة مستخدم / مطوّر",
        "about_team_member_3" to "فارس ضياء – مطوّر",
        "about_team_member_4" to "عمر عطا الله – مطوّر",
        "about_team_member_5" to "سارة عصام – مطوّرة",
        "about_tech_title" to "التقنيات المستخدمة",
        "about_tech_item_1" to "Kotlin, Jetpack Compose, MVVM",
        "about_tech_item_2" to "Room و Firebase Realtime Database",
        "about_tech_item_3" to "Retrofit + Coroutines",
        "about_tech_item_4" to "Navigation Component",
        "about_tech_item_5" to "Figma, Coil, Material 3",
        "about_tech_item_6" to "Git و GitHub و Gradle و Android Studio",
        "about_copyright" to " 2025 SBCO – جميع الحقوق محفوظة",
        "about_technologies" to "التقنيات المستخدمة",
        "about_tech_1" to "Kotlin و Jetpack Compose و MVVM",
        "about_tech_2" to "Room و Firebase Realtime Database",
        "about_tech_3" to "Retrofit و Coroutines",
        "about_tech_4" to "Navigation Component",
    )

    operator fun get(key: String): String? = strings[key]
}

// ==================== GERMAN STRINGS ====================
object StringsDE {
    private val strings = mapOf(
// -------------------- Search --------------------
        "search_title" to "Suche",
        "search_filter_all" to "Alle",
        "search_placeholder" to "Nach Filmen, Serien oder Personen suchen...",
        "search_filter_movies" to "Filme",
        "search_filter_tv" to "Serien",
        "search_filter_people" to "Personen",
        "search_recent_title" to "Letzte Suchanfragen",
        "search_clear_all" to "Alles löschen",
        "search_no_results" to "Keine Ergebnisse gefunden für",
        "search_try_another" to "Versuchen Sie, nach einem anderen Film oder einer Serie zu suchen ",
        "search_start_typing" to "Beginnen Sie mit der Eingabe, um zu suchen ",
        "search_error" to "Fehler: {error}",
        // Detail Screen (extras)
        "detail_discover" to "Entdecken",
        "detail_recommendations_count" to "Empfehlungen {count}",
        "detail_similar_count" to "Ähnlich {count}",
        "detail_no_recommendations" to "Keine Empfehlungen",
        "detail_no_similar" to "Keine Ähnlichen",

        // Detail About extras
        "detail_about_movie" to "Über den Film",
        "detail_status" to "Status",
        "detail_original_language" to "Originalsprache",
        "detail_production_country" to "Produktionsland",
        "detail_production_companies" to "Produktionsfirmen",

        // Settings Screen
        "settings_title" to "Einstellungen",
        "settings_other" to "Weitere Einstellungen",
        "settings_favorites" to "Lieblingsfilme",
        "settings_actors_viewed" to "Actoren gesehen+",
        "settings_movies_viewed" to "Angesehene Filme",
        "settings_password" to "Passwort",
        "settings_notifications" to "Benachrichtigungen",
        "settings_dark_mode" to "Dunkles Design",
        "settings_language" to "Sprache",
        "settings_kids_mode" to "Kindermodus",
        "settings_privacy_policy" to "Datenschutz",
        "settings_help_faq" to "Hilfe / FAQ",
        "settings_about" to "Über",
        "settings_login_prompt" to "Anmelden oder Registrieren",
        "settings_login_subtitle" to "Greifen Sie auf Ihr Konto zu, um Einstellungen zu synchronisieren",

        // Language Settings Screen
        "settings_language_select_title" to "Sprache auswählen",
        "settings_language_info_title" to "Sprachinformationen",
        "settings_language_info_body" to "Ihre bevorzugte Sprache wird sofort in der gesamten App angewendet. Alle Inhalte werden in der gewählten Sprache angezeigt.",
        "settings_language_selected_cd" to "Ausgewählt",

        // About
        "about_title" to "Über",
        "about_app_name" to "Film-Entdeckungs-App",
        "about_app_desc" to "Entdecke mühelos Trending-, beliebte und kommende Filme.",
        "about_objectives" to "Ziele",
        "about_obj_1" to "Erkunde Trending-, beliebte und kommende Filme.",
        "about_obj_2" to "Integriere die TMDB-API für Echtzeitdaten.",
        "about_obj_3" to "Intelligente Suche für schnelle und relevante Ergebnisse.",
        "about_obj_4" to "Speichere und synchronisiere Favoriten mit Room und Firebase.",
        "about_obj_5" to "Elegante Benutzeroberfläche und sanfte Benutzererfahrung mit Jetpack Compose und Material 3.",
        "about_team" to "Team SBCO",
        "about_team_member_1" to "Khalid Hussien – Teamleiter / Entwickler",
        "about_team_member_2" to "Saleh Mohamed – UI/UX-Designer / Entwickler",
        "about_team_member_3" to "Fares Diaa – Entwickler",
        "about_team_member_4" to "Omar Atallah – Entwickler",
        "about_team_member_5" to "Sara Essam – Entwicklerin",
        "about_tech_title" to "Verwendete Technologien",
        "about_tech_item_1" to "Kotlin, Jetpack Compose, MVVM",
        "about_tech_item_2" to "Room & Firebase Realtime Database",
        "about_tech_item_3" to "Retrofit + Coroutines",
        "about_tech_item_4" to "Navigation Component",
        "about_tech_item_5" to "Figma, Coil, Material 3",
        "about_tech_item_6" to "Git & GitHub, Gradle, Android Studio",
        "about_copyright" to " 2025 SBCO – Alle Rechte vorbehalten",
        "about_technologies" to "Verwendete Technologien",
        "about_tech_1" to "Kotlin, Jetpack Compose, MVVM",
        "about_tech_2" to "Room & Firebase Realtime Database",
        "privacy_learn_more_title" to "Mehr erfahren",
        "privacy_learn_more_desc" to "Klicken Sie unten, um die offizielle Datenschutzrichtlinie von TMDb anzuzeigen.",
        "privacy_view_tmdb" to "TMDb-Datenschutzrichtlinie anzeigen",
        "developer_tools" to "Developer Tools",
        // -------------------- Kids --------------------
        // -------------------- Kids Mode --------------------
        "kids_title" to "Kindermodus",
        "kids_search_placeholder" to "Kinderfreundliche Inhalte durchsuchen...",
        "kids_filter_all" to "Alle",
        "kids_filter_movies" to "Filme",
        "kids_filter_tv" to "Serien",
        "genre_filter_newest" to "Neueste",
        "genre_filter_popular" to "Am beliebtesten",
        "genre_filter_family" to "Familienfreundlich",
        "genre_filter_dialog_title" to "Filme filtern",
        "genre_filter_close" to "Schließen",

        // Buttons
        "btn_login" to "Anmelden",
        "btn_signup" to "Registrieren",
        "btn_logout" to "Abmelden",
        "btn_cancel" to "Abbrechen",
        "btn_save" to "Speichern",
        "btn_delete" to "Löschen",
        "btn_retry" to "Erneut versuchen",
        "btn_close" to "Schließen",
        "btn_confirm" to "Bestätigen",
        "btn_back" to "Zurück",
        "btn_next" to "Weiter",
        "btn_previous" to "Zurück",

        // Labels
        "label_username" to "Benutzername",
        "label_email" to "E-Mail",
        "label_password" to "Passwort",
        "label_confirm_password" to "Passwort bestätigen",
        "label_language" to "Sprache",
        "label_theme" to "Design",

        // Messages
        "msg_loading" to "Wird geladen...",
        "msg_success" to "Erfolg",
        "msg_error" to "Fehler",
        "msg_empty" to "Keine Daten verfügbar",
        "msg_no_internet" to "Keine Internetverbindung",
        "msg_refresh" to "Aktualisieren",

        // Validation
        "validation_required" to "Dieses Feld ist erforderlich",
        "validation_invalid_email" to "Ungültige E-Mail-Adresse",
        "validation_password_short" to "Das Passwort muss mindestens 6 Zeichen lang sein",
        "validation_password_mismatch" to "Passwörter stimmen nicht überein",

        // Dialogs
        "dialog_confirm_delete" to "Bist du sicher, dass du dies löschen möchtest?",
        "dialog_confirm_logout" to "Bist du sicher, dass du dich abmelden möchtest?",
        "dialog_confirm_clear_history" to "Bist du sicher, dass du deinen Suchverlauf löschen möchtest?",

        // Navigation
        "nav_home" to "Startseite",
        "nav_movies" to "Filme",
        "nav_actors" to "Schauspieler",
        "nav_search" to "Suche",
        "nav_favorites" to "Favoriten",
        "nav_settings" to "Einstellungen",
        "nav_profile" to "Profil",
        "nav_kids" to "Kinder",
        "nav_history" to "Verlauf",
        "nav_tv" to "TV",

        // Genres
        "genre_action" to "Action",
        "genre_adventure" to "Abenteuer",
        "genre_animation" to "Animation",
        "genre_comedy" to "Komödie",
        "genre_crime" to "Krimi",
        "genre_documentary" to "Dokumentarfilm",
        "genre_drama" to "Drama",
        "genre_family" to "Familie",
        "genre_fantasy" to "Fantasy",
        "genre_history" to "Geschichte",
        "genre_horror" to "Horror",
        "genre_music" to "Musik",
        "genre_mystery" to "Mystery",
        "genre_romance" to "Romanze",
        "genre_science_fiction" to "Science-Fiction",
        "genre_tv_movie" to "Fernsehfilm",
        "genre_thriller" to "Thriller",
        "genre_war" to "Krieg",
        "genre_western" to "Western",

        // Filter Options
        "filter_all_movies" to "Alle Filme",
        "filter_top_rated" to "Top bewertet",
        "filter_newest" to "Neueste",
        "filter_most_popular" to "Am beliebtesten",
        "filter_family_friendly" to "Familienfreundlich",

        // Common (extras)
        "common_loading" to "Wird geladen...",
        "common_error" to "Fehler",
        "common_retry" to "Erneut versuchen",
        "common_close" to "Schließen",
        "common_back" to "Zurück",
        "common_next" to "Weiter",
        "common_previous" to "Zurück",
        "common_cancel" to "Abbrechen",
        "common_confirm" to "Bestätigen",
        "common_delete" to "Löschen",
        "common_save" to "Speichern",
        "common_edit" to "Bearbeiten",
        "common_share" to "Teilen",
        "common_copy" to "Kopieren",
        "common_no_image" to "Kein Bild",
        "common_show_more" to "Mehr anzeigen",
        "common_show_less" to "Weniger anzeigen",
        "common_expand" to "Erweitern",
        "common_collapse" to "Einklappen",
        "home_greeting_night" to "Gute Nacht",
    )

    operator fun get(key: String): String? = strings[key]
}




