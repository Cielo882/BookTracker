package com.cielo.applibros.data.local.dao


import androidx.room.*
import com.cielo.applibros.data.local.entities.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM read_books")
    suspend fun getAllReadBooks(): List<BookEntity>

    @Query("SELECT * FROM read_books WHERE id = :id")
    suspend fun getBookById(id: Int): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Query("DELETE FROM read_books WHERE id = :id")
    suspend fun deleteBook(id: Int)


    @Query("UPDATE read_books SET rating = :rating WHERE id = :bookId")
    suspend fun updateBookRating(bookId: Int, rating: Float)
}