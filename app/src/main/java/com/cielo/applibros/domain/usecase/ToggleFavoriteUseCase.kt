package com.cielo.applibros.domain.usecase

import com.cielo.applibros.domain.repository.BookRepository

class ToggleFavoriteUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: Int, isFavorite: Boolean) {
        repository.updateFavorite(bookId, isFavorite)
    }
}