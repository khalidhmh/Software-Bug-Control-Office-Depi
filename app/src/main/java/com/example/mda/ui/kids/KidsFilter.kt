package com.example.mda.ui.kids

import android.util.Log
import com.example.mda.data.local.entities.MediaEntity

object KidsFilter {

    private const val GENRE_ANIMATION_ID = 16
    private const val GENRE_FAMILY_ID = 10751
    private const val GENRE_KIDS_TV_ID = 10762

    // كلمات تدلّ على أنها تمسّ بالبالغين أو أنمي للكبار
    private val blockedKeywords = listOf(
        "attack on titan", "death note", "tokyo ghoul", "naruto",
        "bleach", "chainsaw man", "demon slayer", "one piece",
        "jujutsu", "hellsing", "high school of the dead",
        "highschool of the dead", "high school dxd", "highschool dxd",
        "dxd", "zombie", "blood", "pervert", "harem", "ecchi",
        "nude", "sexual", "sex", "boob", "panty", "lust",
        "adult", "revenge", "violence", "abuse", "apocalypse"
    )

    // أنواع أو تصنيفات غير مناسبة للأطفال
    private val blockedGenres = listOf(
        "horror", "thriller", "crime", "drama",
        "romance", "documentary", "war", "mystery"
    )

    // الأنواع المسموح بها للأطفال
    private val allowedGenres = listOf("family", "animation", "kids", "child")

    fun isKidsSafe(item: MediaEntity): Boolean {
        val lcTitle = (item.title ?: item.name ?: "").lowercase()
        val lcOverview = (item.overview ?: "").lowercase()
        val genreNames = item.genres?.map { it.lowercase() } ?: emptyList()

        // ❌ محتوى للكبار
        if (item.adult == true) {
            Log.d("KidsFilter", "${item.title} ❌ Rejected: adult flag true")
            return false
        }

        // ❌ كلمات خطيرة في العنوان أو الوصف
        if (blockedKeywords.any { kw ->
                lcTitle.contains(kw) || lcOverview.contains(kw)
            }) {
            Log.d("KidsFilter", "${item.title} ❌ Rejected: blocked keyword")
            return false
        }

        // ❌ تصنيفات غير مناسبة للأطفال
        if (genreNames.any { g ->
                blockedGenres.any { bad -> g.contains(bad) }
            }) {
            Log.d("KidsFilter", "${item.title} ❌ Rejected: blocked genre $genreNames")
            return false
        }

        // ✅ السماح فقط لو Family أو Kids أو Animation بدون كلمات خطيرة
        val byGenreId = item.genreIds?.any {
            it == GENRE_ANIMATION_ID || it == GENRE_FAMILY_ID || it == GENRE_KIDS_TV_ID
        } == true

        val byGenreName = genreNames.any { g ->
            allowedGenres.any { g.contains(it) }
        }

        val byTitle = allowedGenres.any { kw -> lcTitle.contains(kw) }

        val safe = byGenreId || byGenreName || byTitle
        if (!safe) {
            Log.d("KidsFilter", "${item.title} ❌ Rejected: not matching allowed genre")
        } else {
            Log.d("KidsFilter", "${item.title} ✅ Accepted")
        }
        return safe
    }

    fun filterKids(list: List<MediaEntity>): List<MediaEntity> =
        list.filter { isKidsSafe(it) }
}