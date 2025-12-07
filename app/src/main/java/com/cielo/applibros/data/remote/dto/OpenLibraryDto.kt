package com.cielo.applibros.data.remote.dto

import com.google.gson.annotations.SerializedName


data class OpenLibrarySearchResponse(
    @SerializedName("docs") val docs: List<OpenLibraryBookDto>
)

data class OpenLibraryBookDto(
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String?,
    @SerializedName("author_name") val authorName: List<String>?,
    @SerializedName("subject") val subject: List<String>?,
    @SerializedName("language") val language: List<String>?,
    @SerializedName("isbn") val isbn: List<String>?,
    @SerializedName("cover_i") val coverId: Int?
) {
    fun toCoverUrl(): String? {
        return coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
    }
}
