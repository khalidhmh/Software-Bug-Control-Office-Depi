package com.example.mda.localization

/**
 * Centralized Localization Keys for the Movie Discovery App
 * All user-facing strings are defined here with consistent naming convention:
 * - SCREEN_FEATURE_ELEMENT (e.g., HOME_TITLE, SEARCH_PLACEHOLDER)
 * - ERROR_TYPE (e.g., ERROR_INVALID_EMAIL)
 * - BTN_ACTION (e.g., BTN_LOGIN, BTN_SAVE)
 * - LABEL_FIELD (e.g., LABEL_USERNAME)
 * - MSG_TYPE (e.g., MSG_SUCCESS, MSG_LOADING)
 */

object LocalizationKeys {
    // ==================== HOME SCREEN ====================
    const val HOME_GREETING_MORNING = "home_greeting_morning"
    const val HOME_GREETING_AFTERNOON = "home_greeting_afternoon"
    const val HOME_GREETING_EVENING = "home_greeting_evening"
    const val HOME_SUBTITLE = "home_subtitle"
    const val HOME_BANNER_TITLE = "home_banner_title"
    const val HOME_FOR_YOU_TITLE = "home_for_you_title"
    const val HOME_TRENDING_TITLE = "home_trending_title"
    const val HOME_TRENDING_TODAY = "home_trending_today"
    const val HOME_TRENDING_WEEK = "home_trending_week"
    const val HOME_POPULAR_MOVIES = "home_popular_movies"
    const val HOME_POPULAR_TV = "home_popular_tv"
    const val HOME_TOP_RATED = "home_top_rated"

    // ==================== SEARCH SCREEN ====================
    const val SEARCH_TITLE = "search_title"
    const val DEVELOPER_TOOLS = "developer_tools"
    const val SEARCH_FILTER_ALL = "search_filter_all"
    const val SEARCH_PLACEHOLDER = "search_placeholder"
    const val SEARCH_FILTER_MOVIES = "search_filter_movies"
    const val SEARCH_FILTER_TV = "search_filter_tv"
    const val SEARCH_FILTER_PEOPLE = "search_filter_people"
    const val SEARCH_RECENT_TITLE = "search_recent_title"
    const val SEARCH_CLEAR_ALL = "search_clear_all"
    const val SEARCH_NO_RESULTS = "search_no_results"
    const val SEARCH_ERROR = "search_error"

    const val SEARCH_TRY_ANOTHER = "search_try_another"
    const val SEARCH_START_TYPING = "search_start_typing"

    // ==================== MOVIE DETAIL SCREEN ====================
    const val DETAIL_PRODUCTION_DETAILS = "detail_production_details"
    const val DETAIL_SPOKEN_LANGUAGES = "detail_spoken_languages"
    const val DETAIL_PRODUCTION_COUNTRIES = "detail_production_countries"
    const val DETAIL_COMPANIES = "detail_companies"
    const val DETAIL_GENRES = "detail_genres"
    const val DETAIL_KEYWORDS = "detail_keywords"
    const val DETAIL_CAST = "detail_cast"
    const val DETAIL_REVIEWS = "detail_reviews"
    const val DETAIL_VIDEOS = "detail_videos"
    const val DETAIL_RUNTIME = "detail_runtime"
    const val DETAIL_RELEASE_DATE = "detail_release_date"
    const val DETAIL_BUDGET = "detail_budget"
    const val DETAIL_REVENUE = "detail_revenue"
    const val DETAIL_RATING = "detail_rating"
    const val DETAIL_OVERVIEW = "detail_overview"
    const val DETAIL_DISCOVER = "detail_discover"
    const val DETAIL_RECOMMENDATIONS_COUNT = "detail_recommendations_count" // {count}
    const val DETAIL_SIMILAR_COUNT = "detail_similar_count" // {count}
    const val DETAIL_NO_RECOMMENDATIONS = "detail_no_recommendations"
    const val DETAIL_NO_SIMILAR = "detail_no_similar"

    // Extra labels for About section
    const val DETAIL_ABOUT_MOVIE = "detail_about_movie"
    const val DETAIL_STATUS = "detail_status"
    const val DETAIL_ORIGINAL_LANGUAGE = "detail_original_language"
    const val DETAIL_PRODUCTION_COUNTRY_SINGLE = "detail_production_country"
    const val DETAIL_PRODUCTION_COMPANIES = "detail_production_companies"

    // ==================== AUTHENTICATION ====================
    const val AUTH_LOGIN_TITLE = "auth_login_title"
    const val AUTH_SIGNUP_TITLE = "auth_signup_title"
    const val AUTH_AUTHENTICATING = "auth_authenticating"
    const val AUTH_ERROR = "auth_error"
    const val AUTH_ERROR_APPROVE = "auth_error_approve"
    const val AUTH_RETRY = "auth_retry"
    const val AUTH_LOGIN_REQUIRED = "auth_login_required"
    const val AUTH_LOGIN_REQUIRED_MSG = "auth_login_required_msg"

    // ==================== SETTINGS SCREEN ====================
    const val SETTINGS_TITLE = "settings_title"
    const val SETTINGS_OTHER = "settings_other"
    const val SETTINGS_FAVORITES = "settings_favorites"
    const val SETTINGS_ACTORS_VIEWED = "settings_actors_viewed"
    const val SETTINGS_MOVIES_VIEWED = "settings_movies_viewed"
    const val SETTINGS_PASSWORD = "settings_password"
    const val SETTINGS_NOTIFICATIONS = "settings_notifications"
    const val SETTINGS_DARK_MODE = "settings_dark_mode"
    const val SETTINGS_LANGUAGE = "settings_language"
    const val SETTINGS_KIDS_MODE = "settings_kids_mode"
    const val SETTINGS_PRIVACY_POLICY = "settings_privacy_policy"
    const val SETTINGS_HELP_FAQ = "settings_help_faq"
    const val SETTINGS_ABOUT = "settings_about"
    const val SETTINGS_LOGIN_PROMPT = "settings_login_prompt"
    const val SETTINGS_LOGIN_SUBTITLE = "settings_login_subtitle"

    // ==================== LANGUAGE SETTINGS SCREEN ====================
    const val SETTINGS_LANGUAGE_SELECT_TITLE = "settings_language_select_title"
    const val SETTINGS_LANGUAGE_INFO_TITLE = "settings_language_info_title"
    const val SETTINGS_LANGUAGE_INFO_BODY = "settings_language_info_body"
    const val SETTINGS_LANGUAGE_SELECTED_CD = "settings_language_selected_cd"

    // ==================== PROFILE ====================
    const val PROFILE_TITLE = "profile_title"
    const val PROFILE_LOGIN_OR_SIGNUP = "profile_login_or_signup"
    const val PROFILE_ACCESS_ACCOUNT = "profile_access_account"

    // ==================== FAVORITES ====================
    const val FAVORITES_TITLE = "favorites_title"
    const val FAVORITES_EMPTY = "favorites_empty"
    const val FAVORITES_ADD_SUCCESS = "favorites_add_success"
    const val FAVORITES_REMOVE_SUCCESS = "favorites_remove_success"

    // ==================== HELP & FAQ ====================
    const val HELP_TITLE = "help_title"
    const val HELP_FAQ_Q1 = "help_faq_q1"
    const val HELP_FAQ_A1 = "help_faq_a1"
    const val HELP_FAQ_Q2 = "help_faq_q2"
    const val HELP_FAQ_A2 = "help_faq_a2"
    const val HELP_FAQ_Q3 = "help_faq_q3"
    const val HELP_FAQ_A3 = "help_faq_a3"
    const val HELP_FAQ_Q4 = "help_faq_q4"
    const val HELP_FAQ_A4 = "help_faq_a4"
    const val HELP_FAQ_Q5 = "help_faq_q5"
    const val HELP_FAQ_A5 = "help_faq_a5"
    const val HELP_FAQ_Q6 = "help_faq_q6"
    const val HELP_FAQ_A6 = "help_faq_a6"

    // ==================== PRIVACY POLICY ====================
    const val PRIVACY_TITLE = "privacy_title"
    const val PRIVACY_COMMITMENT_TITLE = "privacy_commitment_title"
    const val PRIVACY_COMMITMENT_DESC = "privacy_commitment_desc"
    const val PRIVACY_DATA_TITLE = "privacy_data_title"
    const val PRIVACY_DATA_DESC = "privacy_data_desc"
    const val PRIVACY_MANAGEMENT_TITLE = "privacy_management_title"
    const val PRIVACY_MANAGEMENT_DESC = "privacy_management_desc"
    const val PRIVACY_LEARN_MORE_TITLE = "privacy_learn_more_title"
    const val PRIVACY_LEARN_MORE_DESC = "privacy_learn_more_desc"
    const val PRIVACY_VIEW_TMDB = "privacy_view_tmdb"

    // ==================== ABOUT ====================
    const val ABOUT_TITLE = "about_title"
    const val ABOUT_APP_NAME = "about_app_name"
    const val ABOUT_APP_DESC = "about_app_desc"
    const val ABOUT_OBJECTIVES = "about_objectives"
    const val ABOUT_OBJ_1 = "about_obj_1"
    const val ABOUT_OBJ_2 = "about_obj_2"
    const val ABOUT_OBJ_3 = "about_obj_3"
    const val ABOUT_OBJ_4 = "about_obj_4"
    const val ABOUT_OBJ_5 = "about_obj_5"
    const val ABOUT_TEAM = "about_team"
    const val ABOUT_TEAM_MEMBER_1 = "about_team_member_1"
    const val ABOUT_TEAM_MEMBER_2 = "about_team_member_2"
    const val ABOUT_TEAM_MEMBER_3 = "about_team_member_3"
    const val ABOUT_TEAM_MEMBER_4 = "about_team_member_4"
    const val ABOUT_TEAM_MEMBER_5 = "about_team_member_5"
    const val ABOUT_TECH_TITLE = "about_tech_title"
    const val ABOUT_TECH_ITEM_1 = "about_tech_item_1"
    const val ABOUT_TECH_ITEM_2 = "about_tech_item_2"
    const val ABOUT_TECH_ITEM_3 = "about_tech_item_3"
    const val ABOUT_TECH_ITEM_4 = "about_tech_item_4"
    const val ABOUT_TECH_ITEM_5 = "about_tech_item_5"
    const val ABOUT_TECH_ITEM_6 = "about_tech_item_6"
    const val ABOUT_COPYRIGHT = "about_copyright"

    // ==================== KIDS MODE ====================
    const val KIDS_TITLE = "kids_title"
    const val KIDS_SEARCH_PLACEHOLDER = "kids_search_placeholder"
    const val KIDS_FILTER_MOVIES = "kids_filter_movies"
    const val KIDS_FILTER_TV = "kids_filter_tv"
    const val KIDS_RECENT_SEARCHES = "kids_recent_searches"
    const val KIDS_CLEAR_ALL = "kids_clear_all"
    const val KIDS_NO_RESULTS = "kids_no_results"
    const val KIDS_TRY_ANOTHER = "kids_try_another"

    // ==================== GENRE DETAILS ====================
    const val GENRE_TITLE = "genre_title"
    const val GENRE_FILTER_ALL = "genre_filter_all"
    const val GENRE_FILTER_TOP_RATED = "genre_filter_top_rated"
    const val GENRE_FILTER_NEWEST = "genre_filter_newest"
    const val GENRE_FILTER_POPULAR = "genre_filter_popular"
    const val GENRE_FILTER_FAMILY = "genre_filter_family"
    const val GENRE_FILTER_DIALOG_TITLE = "genre_filter_dialog_title"
    const val GENRE_FILTER_CLOSE = "genre_filter_close"

    // ==================== BUTTONS ====================
    const val BTN_LOGIN = "btn_login"
    const val BTN_SIGNUP = "btn_signup"
    const val BTN_LOGOUT = "btn_logout"
    const val BTN_CANCEL = "btn_cancel"
    const val BTN_SAVE = "btn_save"
    const val BTN_DELETE = "btn_delete"
    const val BTN_RETRY = "btn_retry"
    const val BTN_CLOSE = "btn_close"
    const val BTN_CONFIRM = "btn_confirm"
    const val BTN_BACK = "btn_back"
    const val BTN_NEXT = "btn_next"
    const val BTN_PREVIOUS = "btn_previous"

    // ==================== LABELS ====================
    const val LABEL_USERNAME = "label_username"
    const val LABEL_EMAIL = "label_email"
    const val LABEL_PASSWORD = "label_password"
    const val LABEL_CONFIRM_PASSWORD = "label_confirm_password"
    const val LABEL_LANGUAGE = "label_language"
    const val LABEL_THEME = "label_theme"

    // ==================== MESSAGES ====================
    const val MSG_LOADING = "msg_loading"
    const val MSG_SUCCESS = "msg_success"
    const val MSG_ERROR = "msg_error"
    const val MSG_EMPTY = "msg_empty"
    const val MSG_NO_INTERNET = "msg_no_internet"
    const val MSG_REFRESH = "msg_refresh"

    // ==================== VALIDATION ====================
    const val VALIDATION_REQUIRED = "validation_required"
    const val VALIDATION_INVALID_EMAIL = "validation_invalid_email"
    const val VALIDATION_PASSWORD_SHORT = "validation_password_short"
    const val VALIDATION_PASSWORD_MISMATCH = "validation_password_mismatch"

    // ==================== DIALOGS ====================
    const val DIALOG_CONFIRM_DELETE = "dialog_confirm_delete"
    const val DIALOG_CONFIRM_LOGOUT = "dialog_confirm_logout"
    const val DIALOG_CONFIRM_CLEAR_HISTORY = "dialog_confirm_clear_history"

    // ==================== NAVIGATION ====================
    const val NAV_HOME = "nav_home"
    const val NAV_MOVIES = "nav_movies"
    const val NAV_ACTORS = "nav_actors"
    const val NAV_SEARCH = "nav_search"
    const val NAV_FAVORITES = "nav_favorites"
    const val NAV_SETTINGS = "nav_settings"
    const val NAV_PROFILE = "nav_profile"
    const val NAV_KIDS = "nav_kids"
    const val NAV_HISTORY = "nav_history"

    // ==================== FILTER OPTIONS ====================
    const val FILTER_ALL_MOVIES = "filter_all_movies"
    const val FILTER_TOP_RATED = "filter_top_rated"
    const val FILTER_NEWEST = "filter_newest"
    const val FILTER_MOST_POPULAR = "filter_most_popular"
    const val FILTER_FAMILY_FRIENDLY = "filter_family_friendly"

    // ==================== COMMON ====================
    const val COMMON_LOADING = "common_loading"
    const val COMMON_ERROR = "common_error"
    const val COMMON_RETRY = "common_retry"
    const val COMMON_CLOSE = "common_close"
    const val COMMON_BACK = "common_back"
    const val COMMON_NEXT = "common_next"
    const val COMMON_PREVIOUS = "common_previous"
    const val COMMON_CANCEL = "common_cancel"
    const val COMMON_CONFIRM = "common_confirm"
    const val COMMON_DELETE = "common_delete"
    const val COMMON_SAVE = "common_save"
    const val COMMON_EDIT = "common_edit"
    const val COMMON_SHARE = "common_share"
    const val COMMON_COPY = "common_copy"
    const val COMMON_NO_IMAGE = "common_no_image"
    const val COMMON_SHOW_MORE = "common_show_more"
    const val COMMON_SHOW_LESS = "common_show_less"
    const val COMMON_EXPAND = "common_expand"
    const val COMMON_COLLAPSE = "common_collapse"

    // ==================== PRIVACY POLICY ====================

}
