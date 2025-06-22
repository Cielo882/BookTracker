package com.cielo.applibros.domain.repository

import com.cielo.applibros.domain.model.Book

interface BookRepository {
    suspend fun searchBooks(query: String): List<Book>
    suspend fun getReadBooks(): List<Book>
    suspend fun addToRead(book: Book)
    suspend fun removeFromRead(bookId: Int)
    suspend fun getBookById(id: Int): Book?
    suspend fun updateBookRating(bookId: Int, rating: Float)
}