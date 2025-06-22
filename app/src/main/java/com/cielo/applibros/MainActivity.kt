package com.cielo.applibros

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cielo.applibros.data.local.database.AppDatabase
import com.cielo.applibros.data.remote.api.GutendexApiService
import com.cielo.applibros.data.repository.BookRepositoryImpl
import com.cielo.applibros.domain.usecase.AddToReadUseCase
import com.cielo.applibros.domain.usecase.GetBooksUseCase
import com.cielo.applibros.domain.usecase.GetReadBooksUseCase
import com.cielo.applibros.domain.usecase.RemoveFromReadUseCase
import com.cielo.applibros.domain.usecase.UpdateBookRatingUseCase
import com.cielo.applibros.presentation.adapter.BookAdapter
import com.cielo.applibros.presentation.viewmodel.BookView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var bookAdapter: BookAdapter
    private lateinit var viewModel: BookView
    private var currentTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Forzar modo oscuro
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        setContentView(R.layout.activity_main)

        setupDependencies()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Cargar leidos  al inicio
        viewModel.loadReadBooks()
    }

    private fun setupDependencies() {

        // Configurar OkHttpClient
        val okHttpClient = NetworkHelper.createUnsafeOkHttpClient()

        //  Retrofit config
        val retrofit = Retrofit.Builder()
            .baseUrl(GutendexApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        /*
        // Configurar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(GutendexApiService.Companion.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()*/

        val apiService = retrofit.create(GutendexApiService::class.java)
        val database = AppDatabase.Companion.getDatabase(this)
        val bookDao = database.bookDao()

        // Crear repository y los casos de uso
        val repository = BookRepositoryImpl(apiService, bookDao)
        val getBooksUseCase = GetBooksUseCase(repository)
        val getReadBooksUseCase = GetReadBooksUseCase(repository)
        val addToReadUseCase = AddToReadUseCase(repository)
        val removeFromReadUseCase = RemoveFromReadUseCase(repository)
        val updateBookRatingUseCase = UpdateBookRatingUseCase(repository)


        // (en un proyecto real se usa ViewModelFactory)
        viewModel = BookView(
            getBooksUseCase,
            getReadBooksUseCase,
            addToReadUseCase,
            removeFromReadUseCase,
            updateBookRatingUseCase
        )
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            onBookClick = { book ->
                // Abrir detalles del libro
                AlertDialog.Builder(this)
                    .setTitle(book.title)
                    .setMessage("ID Gutendex API: ${book.id}\n" +
                                "Autor: ${book.authors.joinToString(", ")}\n" +
                            "Idiomas: ${book.languages.joinToString(", ")}"+
                            "Categorias: ${book.subjects.joinToString(", ")}\n"


                    )
                    .setPositiveButton("Cerrar", null)
                    .show()
            },
            onReadClick = { book ->
                if (book.isRead) {
                    viewModel.removeFromRead(book.id)
                } else {
                    viewModel.addToRead(book)
                }
            }, onRatingChanged = { book, rating ->
                viewModel.updateBookRating(book.id, rating)
            }
        )

        findViewById<RecyclerView>(R.id.rvBooks).apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupObservers() {
        viewModel.books.observe(this) { books ->
            if (currentTab == 0) {
                bookAdapter.submitList(books)
            }
        }

        viewModel.readBooks.observe(this) { readBooks ->
            if (currentTab == 1) {
                bookAdapter.submitList(readBooks)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            findViewById<ProgressBar>(R.id.progressBar).visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                // Mostrar error
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            val query = findViewById<TextInputEditText>(R.id.etSearch).text.toString()
            if (query.isNotBlank()) {
                viewModel.searchBooks(query)
            }
        }

        findViewById<TabLayout>(R.id.tabLayout).addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    currentTab = tab?.position ?: 0
                    when (currentTab) {
                        0 -> {
                            // Mostrar resultados de búsqueda
                            viewModel.books.value?.let { bookAdapter.submitList(it) }
                        }
                        1 -> {
                            // Mostrar leidos
                            viewModel.readBooks.value?.let { bookAdapter.submitList(it) }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )
    }
}