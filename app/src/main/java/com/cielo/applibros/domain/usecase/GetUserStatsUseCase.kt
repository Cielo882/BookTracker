package com.cielo.applibros.domain.usecase

import com.cielo.applibros.domain.model.UserStats
import com.cielo.applibros.domain.repository.BookRepository


class GetUserStatsUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(): UserStats {
        val totalRead = repository.getTotalBooksRead()
        val totalToRead = repository.getTotalBooksToRead()
        val averageRating = repository.getAverageRating() ?: 0.0
        val currentlyReading = repository.getCurrentlyReading()
        val favorites = repository.getFavoriteBooks()

        return UserStats(
            totalBooksRead = totalRead,
            totalBooksToRead = totalToRead,
            averageRating = averageRating,
            currentlyReading = currentlyReading,
            favoriteBooks = favorites
        )
    }
}