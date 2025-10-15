package com.example.mda.data.local.converters

import androidx.room.TypeConverter

class Converters {

    // List<Int> <-> String
    @TypeConverter
    fun fromIntList(list: List<Int>?): String? = list?.joinToString(",")

    @TypeConverter
    fun toIntList(data: String?): List<Int>? =
        data?.split(",")?.mapNotNull { it.toIntOrNull() }

    // List<String> <-> String
    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.joinToString(",")

    @TypeConverter
    fun toStringList(data: String?): List<String>? =
        data?.split(",")?.map { it.trim() }
}
