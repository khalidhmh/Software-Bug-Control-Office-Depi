package com.example.mda.ui.kids

import com.example.mda.data.local.entities.MediaEntity

object KidsFilter {
    // TMDB Animation genre id
    private const val GENRE_ANIMATION_ID = 16
    private const val GENRE_FAMILY_ID = 10751

    private val keywordWhitelist = listOf(
        "animation", "anime", "cartoon", "family"
    )

    fun isKidsSafe(item: MediaEntity): Boolean {
        if (item.adult == true) return false
        // Prefer explicit genreIds when present
        val byId = item.genreIds?.any { it == GENRE_ANIMATION_ID || it == GENRE_FAMILY_ID } == true
        val byNames = item.genres?.any { g ->
            val name = g.lowercase()
            keywordWhitelist.any { kw -> name.contains(kw) }
        } == true
        val byTitleHeuristic = listOfNotNull(item.title, item.name)
            .any { t ->
                val s = t.lowercase()
                keywordWhitelist.any { kw -> s.contains(kw) }
            }
        return byId || byNames || byTitleHeuristic
    }

    fun filterKids(list: List<MediaEntity>): List<MediaEntity> = list.filter { isKidsSafe(it) }
}
