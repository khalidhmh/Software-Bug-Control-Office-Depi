package com.example.mda.data.repository

import android.util.Log
import com.example.mda.data.local.dao.MediaDao
import com.example.mda.data.local.entities.MediaEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.MovieDetailsResponse
import com.example.mda.data.remote.model.ReleaseDatesResponse
import com.example.mda.data.remote.model.WatchProviderCountry
import com.example.mda.data.remote.model.WatchProvidersResponse
import com.example.mda.data.remote.model.ReviewsResponse
import com.example.mda.data.remote.model.KeywordsResponse
import com.example.mda.data.repository.mappers.toMediaEntity
import com.example.mda.localization.LanguageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "MovieDetailsRepository"
private const val TMDB_API_KEY = "53b57a02de510f8c255d80f88e705cf2"

class MovieDetailsRepository(
    private val apiService: TmdbApi,
    private val mediaDao: MediaDao
) {

    suspend fun getCachedMovie(id: Int): MediaEntity? = withContext(Dispatchers.IO) {
        mediaDao.getById(id, type = "movie")
    }

// Only override language-dependent UI text (e.g., overview)
private fun overrideTextOnly(baseEn: MediaEntity, localized: MediaEntity): MediaEntity {
    return baseEn.copy(
        overview = localized.overview?.takeIf { it.isNotBlank() } ?: baseEn.overview
    )
}

    suspend fun getCachedTv(id: Int): MediaEntity? = withContext(Dispatchers.IO) {
        mediaDao.getById(id, type = "tv")
    }

    suspend fun getMovieById(id: Int): MediaEntity? = withContext(Dispatchers.IO) {
        Log.d(TAG, "🎬 Fetching movie details for ID: $id")
        try {
            // 1) Always get English as the base to keep consistent metadata across languages
            val enResp = apiService.getMovieDetails(
                movieId = id,
                language = "en",
                apiKey = TMDB_API_KEY
            )

            if (enResp.isSuccessful) {
                val enBody: MovieDetailsResponse? = enResp.body()
                Log.d(TAG, "✅ Movie EN response: ${enBody?.title}")
                Log.d(TAG, "📊 Credits: ${enBody?.credits?.cast?.size ?: 0} cast members")
                Log.d(TAG, "🎥 Videos: ${enBody?.videos?.results?.size ?: 0} videos")

                var entity = enBody?.toMediaEntity("movie") ?: return@withContext null

                // 2) If current language is not English, fetch localized and override text-only fields
                if (LanguageProvider.currentCode != "en") {
                    runCatching {
                        val locResp = apiService.getMovieDetails(
                            movieId = id,
                            // no explicit language -> interceptor adds selected language
                            apiKey = TMDB_API_KEY
                        )
                        if (locResp.isSuccessful) {
                            locResp.body()?.toMediaEntity("movie")?.let { locEntity ->
                                entity = overrideTextOnly(entity, locEntity)
                            }
                        }
                    }.onFailure { Log.w(TAG, "Localized fetch failed: ${it.message}") }
                }

                // الحفاظ على حالة المفضلة و الـ Watchlist
                val existingEntity = mediaDao.getByIdOnly(id)
                val finalEntity = if (existingEntity != null) {
                    entity.copy(
                        isFavorite = existingEntity.isFavorite,
                        isInWatchlist = existingEntity.isInWatchlist
                    )
                } else {
                    entity
                }

                Log.d(TAG, "💾 Saving to database: Cast=${finalEntity.cast?.size}, Videos=${finalEntity.videos?.size}, isFavorite=${finalEntity.isFavorite}")
                mediaDao.upsert(finalEntity)
                finalEntity
            } else {
                Log.e(TAG, "❌ API Error: ${enResp.code()} - ${enResp.message()}")
                throw Exception("Failed to load movie details: ${enResp.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in getMovieById: ${e.message}", e)
            throw e
        }
    }

    suspend fun getTvById(id: Int): MediaEntity? = withContext(Dispatchers.IO) {
        Log.d(TAG, "📺 Fetching TV details for ID: $id")
        try {
            // 1) Always get English base
            val enResp = apiService.getTvDetails(
                tvId = id,
                language = "en",
                apiKey = TMDB_API_KEY
            )

            if (enResp.isSuccessful) {
                val enBody: MovieDetailsResponse? = enResp.body()
                Log.d(TAG, "✅ TV EN response: ${enBody?.title}")
                Log.d(TAG, "📊 Credits: ${enBody?.credits?.cast?.size ?: 0} cast members")
                Log.d(TAG, "🎥 Videos: ${enBody?.videos?.results?.size ?: 0} videos")

                var entity = enBody?.toMediaEntity("tv") ?: return@withContext null

                // 2) Overlay localized text-only fields
                if (LanguageProvider.currentCode != "en") {
                    runCatching {
                        val locResp = apiService.getTvDetails(
                            tvId = id,
                            apiKey = TMDB_API_KEY
                        )
                        if (locResp.isSuccessful) {
                            locResp.body()?.toMediaEntity("tv")?.let { locEntity ->
                                entity = overrideTextOnly(entity, locEntity)
                            }
                        }
                    }.onFailure { Log.w(TAG, "Localized fetch (TV) failed: ${it.message}") }
                }

                // الحفاظ على حالة المفضلة و الـ Watchlist
                val existingEntity = mediaDao.getByIdOnly(id)
                val finalEntity = if (existingEntity != null) {
                    entity.copy(
                        isFavorite = existingEntity.isFavorite,
                        isInWatchlist = existingEntity.isInWatchlist
                    )
                } else {
                    entity
                }

                Log.d(TAG, "💾 Saving to database: Cast=${finalEntity.cast?.size}, Videos=${finalEntity.videos?.size}, isFavorite=${finalEntity.isFavorite}")
                mediaDao.upsert(finalEntity)
                finalEntity
            } else {
                Log.e(TAG, "❌ API Error: ${enResp.code()} - ${enResp.message()}")
                throw Exception("Failed to load tv details: ${enResp.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in getTvById: ${e.message}", e)
            throw e
        }
    }

    // ================= Similar =================
    suspend fun getSimilarMovies(id: Int): List<MediaEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiService.getSimilarMovies(movieId = id)
            if (response.isSuccessful) {
                val body = response.body()?.results ?: emptyList()
                body.map { it.toMediaEntity("movie") }
            } else emptyList()
        }.getOrElse { emptyList() }
    }

    suspend fun getSimilarTvShows(id: Int): List<MediaEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiService.getSimilarTvShows(tvId = id)
            if (response.isSuccessful) {
                val body = response.body()?.results ?: emptyList()
                body.map { it.toMediaEntity("tv") }
            } else emptyList()
        }.getOrElse { emptyList() }
    }

    // ================= Recommendations =================
    suspend fun getRecommendedMovies(id: Int): List<MediaEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiService.getRecommendedMovies(movieId = id)
            if (response.isSuccessful) {
                val body = response.body()?.results ?: emptyList()
                body.map { it.toMediaEntity("movie") }
            } else emptyList()
        }.getOrElse { emptyList() }
    }

    suspend fun getRecommendedTvShows(id: Int): List<MediaEntity> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiService.getRecommendedTvShows(tvId = id)
            if (response.isSuccessful) {
                val body = response.body()?.results ?: emptyList()
                body.map { it.toMediaEntity("tv") }
            } else emptyList()
        }.getOrElse { emptyList() }
    }

    // ================= Watch Providers =================
    data class ProvidersGrouped(
        val link: String?,
        val buy: List<ProviderLogo>,
        val rent: List<ProviderLogo>,
        val stream: List<ProviderLogo>
    )

    data class ProviderLogo(val name: String, val logoPath: String?)

    private fun WatchProvidersResponse.groupFor(country: String): ProvidersGrouped? {
        val c: WatchProviderCountry = results[country] ?: return null
        return ProvidersGrouped(
            link = c.link,
            buy = (c.buy ?: emptyList()).map { ProviderLogo(it.providerName, it.logoPath) },
            rent = (c.rent ?: emptyList()).map { ProviderLogo(it.providerName, it.logoPath) },
            stream = (c.flatrate ?: emptyList()).map { ProviderLogo(it.providerName, it.logoPath) }
        )
    }

    suspend fun getMovieProviders(id: Int, country: String = "US"): ProvidersGrouped? = withContext(Dispatchers.IO) {
        runCatching {
            val resp = apiService.getMovieWatchProviders(id)
            if (resp.isSuccessful) resp.body()?.groupFor(country) else null
        }.getOrNull()
    }

    suspend fun getTvProviders(id: Int, country: String = "US"): ProvidersGrouped? = withContext(Dispatchers.IO) {
        runCatching {
            val resp = apiService.getTvWatchProviders(id)
            if (resp.isSuccessful) resp.body()?.groupFor(country) else null
        }.getOrNull()
    }

    // ================= Reviews & Keywords =================
    suspend fun getMovieReviews(id: Int): ReviewsResponse? = withContext(Dispatchers.IO) {
        runCatching { apiService.getMovieReviews(id) }.getOrNull()?.body()
    }

    suspend fun getTvReviews(id: Int): ReviewsResponse? = withContext(Dispatchers.IO) {
        runCatching { apiService.getTvReviews(id) }.getOrNull()?.body()
    }

    suspend fun getMovieKeywords(id: Int): KeywordsResponse? = withContext(Dispatchers.IO) {
        runCatching { apiService.getMovieKeywords(id) }.getOrNull()?.body()
    }

    suspend fun getTvKeywords(id: Int): KeywordsResponse? = withContext(Dispatchers.IO) {
        runCatching { apiService.getTvKeywords(id) }.getOrNull()?.body()
    }

    // ================= Release Dates (Movies) =================
    data class ReleaseSummary(
        val certification: String? = null,
        val theatrical: String? = null,
        val digital: String? = null,
        val stream: String? = null
    )

    suspend fun getMovieReleaseSummary(id: Int, country: String = "US"): ReleaseSummary? = withContext(Dispatchers.IO) {
        runCatching {
            val resp = apiService.getMovieReleaseDates(id)
            if (!resp.isSuccessful) return@withContext null
            val body: ReleaseDatesResponse = resp.body() ?: return@withContext null
            val node = body.results.firstOrNull { it.iso31661.equals(country, true) }
                ?: body.results.firstOrNull()
                ?: return@withContext null

            // TMDB types: 1=Premiere,2=Theatrical (limited),3=Theatrical,4=Digital,5=Physical,6=TV
            val theatrical = node.releaseDates.firstOrNull { it.type == 3 }?.releaseDate
                ?: node.releaseDates.firstOrNull { it.type == 2 }?.releaseDate
            val digital = node.releaseDates.firstOrNull { it.type == 4 }?.releaseDate
            val stream = node.releaseDates.firstOrNull { it.type == 6 }?.releaseDate
            val cert = node.releaseDates.firstOrNull { !it.certification.isNullOrBlank() }?.certification

            ReleaseSummary(
                certification = cert,
                theatrical = theatrical,
                digital = digital,
                stream = stream
            )
        }.getOrNull()
    }
}


// 🔹 Mapper لتحويل MovieDetailsResponse لـ MediaEntity
private fun mergeEntities(primary: MediaEntity, fallback: MediaEntity): MediaEntity {
    fun <T> pick(p: T?, f: T?): T? = p ?: f
    fun pickText(p: String?, f: String?): String? = if (!p.isNullOrBlank()) p else f
    fun <T> pickList(p: List<T>?, f: List<T>?): List<T>? = if (!p.isNullOrEmpty()) p else f

    return primary.copy(
        title = pickText(primary.title, fallback.title),
        name = pickText(primary.name, fallback.name),
        overview = pickText(primary.overview, fallback.overview),
        genres = pickList(primary.genres, fallback.genres),
        spokenLanguages = pickList(primary.spokenLanguages, fallback.spokenLanguages),
        productionCompanies = pickList(primary.productionCompanies, fallback.productionCompanies),
        productionCountries = pickList(primary.productionCountries, fallback.productionCountries),
        runtime = pick(primary.runtime, fallback.runtime),
        tagline = pickText(primary.tagline, fallback.tagline),
        status = pickText(primary.status, fallback.status)
    )
}

