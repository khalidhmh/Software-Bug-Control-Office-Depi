package com.example.mda.data.remote.model

import com.google.gson.annotations.SerializedName

data class KeywordsResponse(
    @SerializedName("keywords") val keywords: List<KeywordItem>? = null,
    // Some TV endpoints return {results:[{id,name}]}
    @SerializedName("results") val results: List<KeywordItem>? = null
) {
    fun all(): List<KeywordItem> = keywords ?: results ?: emptyList()
}

data class KeywordItem(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
