package com.cielo.applibros.domain.usecase

import com.cielo.applibros.domain.repository.BookRepository

class UpdateBookReviewUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: Int, review: String?) {
        repository.updateReview(bookId, review)
    }
}