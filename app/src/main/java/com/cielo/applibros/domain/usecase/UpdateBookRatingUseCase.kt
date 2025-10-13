package com.cielo.applibros.domain.usecase

import com.cielo.applibros.domain.repository.BookRepository

class UpdateBookRatingUseCase(  private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: Int, rating: Int) {
        repository.updateRating(bookId, rating)
    }
}