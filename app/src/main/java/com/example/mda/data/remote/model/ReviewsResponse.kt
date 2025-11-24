package com.example.mda.data.remote.model

import com.google.gson.annotations.SerializedName

data class ReviewsResponse(
    @SerializedName("results") val results: List<ReviewItem> = emptyList()
)

data class ReviewItem(
    @SerializedName("author") val author: String?,
    @SerializedName("author_details") val authorDetails: AuthorDetails?,
    @SerializedName("content") val content: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class AuthorDetails(
    @SerializedName("rating") val rating: Double?
)
