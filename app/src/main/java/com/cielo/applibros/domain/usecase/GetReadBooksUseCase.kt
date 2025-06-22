package com.cielo.applibros.domain.usecase

import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.domain.repository.BookRepository

class GetReadBooksUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(): List<Book> {
        return repository.getReadBooks()
    }
}