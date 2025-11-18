package com.cielo.applibros.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.domain.model.ReadingStatus
import com.cielo.applibros.domain.usecase.AddToReadUseCase
import com.cielo.applibros.domain.usecase.GetBooksToReadUseCase
import com.cielo.applibros.domain.usecase.GetBooksUseCase
import com.cielo.applibros.domain.usecase.GetCurrentlyReadingUseCase
import com.cielo.applibros.domain.usecase.GetFinishedBooksUseCase
import com.cielo.applibros.domain.usecase.GetReadBooksUseCase
import com.cielo.applibros.domain.usecase.RemoveFromReadUseCase
import com.cielo.applibros.domain.usecase.ToggleFavoriteUseCase
import com.cielo.applibros.domain.usecase.UpdateBookRatingUseCase
import com.cielo.applibros.domain.usecase.UpdateBookReviewUseCase
import com.cielo.applibros.domain.usecase.UpdateBookStatusUseCase
import com.cielo.applibros.domain.usecase.UpdateFinishDateUseCase
import com.cielo.applibros.domain.usecase.UpdateStartDateUseCase
import kotlinx.coroutines.launch

class BookViewModelUpdated(
    private val getBooksUseCase: GetBooksUseCase,
    private val getReadBooksUseCase: GetReadBooksUseCase,
    private val addToReadUseCase: AddToReadUseCase,
    private val removeFromReadUseCase: RemoveFromReadUseCase,
    private val updateBookRatingUseCase: UpdateBookRatingUseCase,
    // Nuevos use cases
    private val updateBookStatusUseCase: UpdateBookStatusUseCase,
    private val updateBookReviewUseCase: UpdateBookReviewUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getBooksToReadUseCase: GetBooksToReadUseCase,
    private val getCurrentlyReadingUseCase: GetCurrentlyReadingUseCase,
    private val getFinishedBooksUseCase: GetFinishedBooksUseCase,
    private val updateStartDateUseCase: UpdateStartDateUseCase,
    private val updateFinishDateUseCase: UpdateFinishDateUseCase
) : ViewModel() {

    // LiveData para resultados de búsqueda
    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    private val _readBooks = MutableLiveData<List<Book>>()
    val readBooks: LiveData<List<Book>> = _readBooks

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // LiveData observables directos de la base de datos
    val booksToRead: LiveData<List<Book>> = getBooksToReadUseCase.observeBooksToRead()
    val currentlyReading: LiveData<List<Book>> = getCurrentlyReadingUseCase.observeCurrentlyReading()
    val finishedBooks: LiveData<List<Book>> = getFinishedBooksUseCase.observeFinishedBooks()

    // Métodos existentes
    fun searchBooks(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val result = getBooksUseCase(query)
                _books.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadReadBooks() {
        viewModelScope.launch {
            try {
                val result = getReadBooksUseCase()
                _readBooks.value = result
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addToRead(book: Book) {
        viewModelScope.launch {
            try {
                addToReadUseCase(book)
                // Las LiveData se actualizarán automáticamente
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun removeFromRead(book: Book) {
        viewModelScope.launch {
            try {
                removeFromReadUseCase(book)
                loadReadBooks()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


    fun updateBookRating(bookId: Int, rating: Int) {
        viewModelScope.launch {
            try {
                updateBookRatingUseCase(bookId, rating)
                loadBooksByStatus(ReadingStatus.FINISHED)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Nuevos métodos
    fun updateBookStatus(bookId: Int, status: ReadingStatus) {
        viewModelScope.launch {
            try {
                updateBookStatusUseCase(bookId, status)
                // Las LiveData se actualizarán automáticamente
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateBookReview(bookId: Int, review: String?) {
        viewModelScope.launch {
            try {
                updateBookReviewUseCase(bookId, review)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleFavorite(bookId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(bookId, isFavorite)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateStartDate(bookId: Int, startDate: Long) {
        viewModelScope.launch {
            try {
                updateStartDateUseCase(bookId, startDate)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateFinishDate(bookId: Int, finishDate: Long) {
        viewModelScope.launch {
            try {
                updateFinishDateUseCase(bookId, finishDate)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // Método para cargar libros por estado (opcional, ya que las LiveData se actualizan automáticamente)
    fun loadBooksByStatus(status: ReadingStatus) {
        // No es necesario hacer nada aquí, las LiveData se mantienen actualizadas automáticamente
        // Pero puedes usar este método para forzar una actualización si es necesario
    }
}