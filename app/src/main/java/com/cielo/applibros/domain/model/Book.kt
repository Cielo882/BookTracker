package com.cielo.applibros.domain.model

data class Book(
    val id: Int,
    val title: String,
    val authors: List<String>,
    val subjects: List<String>,
    val languages: List<String>,
    val formats: Map<String, String>,

    // Nuevos campos
    val readingStatus: ReadingStatus = ReadingStatus.TO_READ,
    val rating: Int? = null,
    val startDate: Long? = null,
    val finishDate: Long? = null,
    val review: String? = null,
    val isFavorite: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
) {
    // Propiedades útiles
    val authorsString: String
        get() = authors.joinToString(", ")

    val hasImage: Boolean
        get() = formats.containsKey("image/jpeg")

    val imageUrl: String?
        get() = formats["image/jpeg"]

    val isReading: Boolean
        get() = readingStatus == ReadingStatus.READING

    val isFinished: Boolean
        get() = readingStatus == ReadingStatus.FINISHED

    val isToRead: Boolean
        get() = readingStatus == ReadingStatus.TO_READ
}