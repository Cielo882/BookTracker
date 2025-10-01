package com.cielo.applibros.domain.usecase

import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.domain.repository.BookRepository

class GetBooksUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(query: String): List<Book> {
        return repository.searchBooks(query)
    }
}