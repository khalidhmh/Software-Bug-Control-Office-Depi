package com.example.mda.ui.kids

sealed class KidsScreens(val route: String) {
    data object Splash : KidsScreens("kids_splash")
    data object Home : KidsScreens("kids_home")
    data object Search : KidsScreens("kids_search")
    data object Favorites : KidsScreens("kids_favorites")
    data object Section : KidsScreens("kids_section/{category}")
}
