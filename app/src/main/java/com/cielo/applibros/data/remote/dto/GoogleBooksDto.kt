package com.cielo.applibros.data.remote.dto


import com.google.gson.annotations.SerializedName

data class GoogleBooksSearchResponse(
    @SerializedName("items") val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    @SerializedName("id") val id: String,
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    @SerializedName("title") val title: String?,
    @SerializedName("authors") val authors: List<String>?,
    @SerializedName("categories") val categories: List<String>?,
    @SerializedName("language") val language: String?,
    @SerializedName("imageLinks") val imageLinks: ImageLinks?
)

data class ImageLinks(
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("smallThumbnail") val smallThumbnail: String?
)
