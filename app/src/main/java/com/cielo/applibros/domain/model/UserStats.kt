package com.cielo.applibros.domain.model

data class UserStats(
    val totalBooksRead: Int = 0,
    val totalBooksToRead: Int = 0,
    val averageRating: Double = 0.0,
    val currentlyReading: List<Book> = emptyList(),
    val recentlyFinishedBooks: List<Book> = emptyList(),
    //val favoriteBooks: List<Book>
)