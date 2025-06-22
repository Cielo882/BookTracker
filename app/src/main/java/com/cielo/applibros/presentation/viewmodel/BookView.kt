package com.cielo.applibros.presentation.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.domain.usecase.AddToReadUseCase
import com.cielo.applibros.domain.usecase.GetBooksUseCase
import com.cielo.applibros.domain.usecase.GetReadBooksUseCase
import com.cielo.applibros.domain.usecase.RemoveFromReadUseCase
import com.cielo.applibros.domain.usecase.UpdateBookRatingUseCase
import kotlinx.coroutines.launch

class BookView(
    private val getBooksUseCase: GetBooksUseCase,
    private val getReadBooksUseCase: GetReadBooksUseCase,
    private val addToReadUseCase: AddToReadUseCase,
    private val removeFromReadUseCase: RemoveFromReadUseCase,
    private val updateBookRatingUseCase: UpdateBookRatingUseCase

) : ViewModel() {

    private val _books = MutableLiveData<List<Book>>()
    val books: MutableLiveData<List<Book>> = _books

    private val _readBooks = MutableLiveData<List<Book>>()
    val readBooks: MutableLiveData<List<Book>> = _readBooks

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> = _error

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = getBooksUseCase(query)
                _books.value = result
                _error.value = null
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
                loadReadBooks()
                //  actualizar la lista de búsqueda también
                _books.value?.let { currentBooks ->
                    _books.value = currentBooks.map {
                        if (it.id == book.id) it.copy(isRead = true) else it
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun removeFromRead(bookId: Int) {
        viewModelScope.launch {
            try {
                removeFromReadUseCase(bookId)
                loadReadBooks()
                //  actualizar la lista de búsqueda también
                _books.value?.let { currentBooks ->
                    _books.value = currentBooks.map {
                        if (it.id == bookId) it.copy(isRead = false, rating = 0f) else it
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    //actualizar rating
    fun updateBookRating(bookId: Int, rating: Float) {
        viewModelScope.launch {
            try {
                updateBookRatingUseCase(bookId, rating)
                loadReadBooks() // Recargar Leidos para mostrar nuevo rating

                _books.value?.let { currentBooks ->
                    _books.value = currentBooks.map {
                        if (it.id == bookId) it.copy(rating = rating) else it
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}