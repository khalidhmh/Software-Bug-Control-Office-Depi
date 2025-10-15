package com.example.mda.data.repository

import android.util.Log
import com.example.mda.data.local.dao.ActorDao
import com.example.mda.data.local.entities.ActorEntity
import com.example.mda.data.remote.api.TmdbApi
import com.example.mda.data.remote.model.Actor
import com.example.mda.data.remote.model.ActorFullDetails
import com.example.mda.data.remote.model.ActorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * ✅ Khalid: Repository for fetching popular actors, caching them,
 * and retrieving full actor details with offline fallback.
 */
class ActorsRepository(
    private val api: TmdbApi,
    private val actorDao: ActorDao? = null
) {

    // ✅ 1. Fetch list of popular actors (API direct)
    suspend fun getPopularActors(page: Int = 1): Response<ActorResponse> =
        withContext(Dispatchers.IO) {
            Log.d("ActorsRepo", "getPopularActors called, page=$page")
            val response = api.getPopularPeople(page = page)
            Log.d("ActorsRepo", "API response success=${response.isSuccessful}")
            response
        }

    /**
     * ✅ 2. Fetch actors with cache fallback
     * Try API first — if fails, fallback to cache.
     */
    suspend fun getPopularActorsWithCache(page: Int = 1): List<ActorEntity> =
        withContext(Dispatchers.IO) {
            Log.d("ActorsRepo", "getPopularActorsWithCache called, page=$page")
            try {
                val response = api.getPopularPeople(page = page)
                Log.d("ActorsRepo", "API call completed, success=${response.isSuccessful}")

                if (response.isSuccessful) {
                    val actors = response.body()?.results ?: emptyList()
                    Log.d("ActorsRepo", "Fetched ${actors.size} actors from API")
                    cacheActors(actors)
                    actors.map {
                        ActorEntity(
                            id = it.id,
                            name = it.name,
                            profilePath = it.profilePath,
                            biography = it.biography,
                            birthday = it.birthday,
                            placeOfBirth = it.placeOfBirth
                        )
                    }
                } else {
                    Log.d("ActorsRepo", "API error, using cache")
                    actorDao?.getAllActors()?.also { Log.d("ActorsRepo", "Fetched ${it.size} actors from cache") } ?: emptyList()
                }
            } catch (e: Exception) {
                Log.d("ActorsRepo", "Exception in API call: ${e.localizedMessage}, using cache")
                actorDao?.getAllActors()?.also { Log.d("ActorsRepo", "Fetched ${it.size} actors from cache") } ?: emptyList()
            }
        }

    /**
     * ✅ 3. Cache list of actors (used after API success)
     */
    suspend fun cacheActors(actors: List<Actor>) = withContext(Dispatchers.IO) {
        if (actorDao == null) {
            Log.d("ActorsRepo", "cacheActors skipped: actorDao is null")
            return@withContext
        }
        Log.d("ActorsRepo", "Caching ${actors.size} actors")
        actors.forEach { actor ->
            val entity = ActorEntity(
                id = actor.id,
                name = actor.name,
                profilePath = actor.profilePath,
                biography = actor.biography,
                birthday = actor.birthday,
                placeOfBirth = actor.placeOfBirth
            )
            actorDao.upsert(entity)
            Log.d("ActorsRepo", "Cached actor: ${actor.name}")
        }
    }

    /**
     * ✅ 4. Get all cached actors (used when no internet)
     */
    suspend fun getCachedActors(): List<ActorEntity> = withContext(Dispatchers.IO) {
        val cached = actorDao?.getAllActors() ?: emptyList()
        Log.d("ActorsRepo", "getCachedActors returned ${cached.size} actors")
        cached
    }

    /**
     * ✅ 5. Get full actor details by ID (API → Cache fallback)
     */
    suspend fun getFullActorDetails(personId: Int, forceRefresh: Boolean = false): ActorFullDetails? =
        withContext(Dispatchers.IO) {
            Log.d("ActorsRepo", "getFullActorDetails called, personId=$personId, forceRefresh=$forceRefresh")
            try {
                val response = api.getActorDetails(
                    personId = personId,
                    appendToResponse = "combined_credits,external_ids"
                )
                Log.d("ActorsRepo", "API call completed, success=${response.isSuccessful}")

                if (response.isSuccessful) {
                    val details = response.body()
                    Log.d("ActorsRepo", "Fetched details for actor: ${details?.name}")
                    details?.let {
                        actorDao?.upsert(
                            ActorEntity(
                                id = it.id,
                                name = it.name ?: "",
                                profilePath = it.profile_path,
                                biography = it.biography,
                                birthday = it.birthday,
                                placeOfBirth = it.place_of_birth
                            )
                        )
                        Log.d("ActorsRepo", "Actor details cached")
                    }
                    details
                } else {
                    Log.d("ActorsRepo", "API error, using cache")
                    actorDao?.getDetails(personId)?.let { cached ->
                        Log.d("ActorsRepo", "Fetched details from cache for actor: ${cached.name}")
                        ActorFullDetails(
                            id = cached.id,
                            name = cached.name,
                            biography = cached.biography,
                            birthday = cached.birthday,
                            profile_path = cached.profilePath,
                            place_of_birth = cached.placeOfBirth,
                            combined_credits = null,
                            external_ids = null,
                            gender = null,
                            homepage = null,
                            images = null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("ActorsRepo", "Exception in getFullActorDetails: ${e.localizedMessage}, using cache")
                actorDao?.getDetails(personId)?.let { cached ->
                    Log.d("ActorsRepo", "Fetched details from cache for actor: ${cached.name}")
                    return@withContext ActorFullDetails(
                        id = cached.id,
                        name = cached.name,
                        biography = cached.biography,
                        birthday = cached.birthday,
                        profile_path = cached.profilePath,
                        place_of_birth = cached.placeOfBirth,
                        combined_credits = null,
                        external_ids = null,
                        gender = null,
                        homepage = null,
                        images = null
                    )
                }
                null
            }
        }
}
