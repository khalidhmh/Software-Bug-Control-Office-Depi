package com.example.mda.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.mda.data.remote.model.Genre

class Converters {

    private val gson = Gson()

    // ✅ List<Int> <-> String
    @TypeConverter
    fun fromIntList(list: List<Int>?): String? = list?.joinToString(",")

    @TypeConverter
    fun toIntList(data: String?): List<Int>? =
        data?.split(",")?.mapNotNull { it.toIntOrNull() }

    // ✅ List<String> <-> String
    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.joinToString(",")

    @TypeConverter
    fun toStringList(data: String?): List<String>? =
        data?.split(",")?.map { it.trim() }

    // ✅ جديد: List<Genre> <-> JSON
    @TypeConverter
    fun fromGenreList(genres: List<Genre>?): String {
        return gson.toJson(genres)
    }

    @TypeConverter
    fun toGenreList(json: String?): List<Genre> {
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(json, type)
    }
}