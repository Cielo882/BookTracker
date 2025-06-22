package com.cielo.applibros.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("authors") val authors: List<AuthorDto>,
    @SerializedName("subjects") val subjects: List<String>,
    @SerializedName("languages") val languages: List<String>,
    @SerializedName("download_count") val downloadCount: Int,
    @SerializedName("formats") val formats: Map<String, String>
)

data class AuthorDto(
    @SerializedName("name") val name: String
)

data class BookSearchResponse(
    @SerializedName("results") val results: List<BookDto>
)
