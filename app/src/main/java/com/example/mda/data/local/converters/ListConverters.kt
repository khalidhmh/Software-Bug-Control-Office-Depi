package com.example.mda.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.mda.data.local.entities.Cast
import com.example.mda.data.local.entities.Video

class Converters {

    // Int list <-> String
    @TypeConverter
    fun fromIntList(list: List<Int>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.split(",")?.mapNotNull { it.toIntOrNull() }
    }

    // String list <-> String
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")
    }

    // Cast list <-> JSON
    private val gson = Gson()

    @TypeConverter
    fun fromCastList(cast: List<Cast>?): String? {
        return gson.toJson(cast)
    }

    @TypeConverter
    fun toCastList(json: String?): List<Cast>? {
        if (json.isNullOrEmpty()) return null
        val type = object : TypeToken<List<Cast>>() {}.type
        return gson.fromJson(json, type)
    }

    // Video list <-> JSON
    @TypeConverter
    fun fromVideoList(videos: List<Video>?): String? {
        return gson.toJson(videos)
    }

    @TypeConverter
    fun toVideoList(json: String?): List<Video>? {
        if (json.isNullOrEmpty()) return null
        val type = object : TypeToken<List<Video>>() {}.type
        return gson.fromJson(json, type)
    }
}
