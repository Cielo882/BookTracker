package com.cielo.applibros

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cielo.applibros.data.local.database.AppDatabase
import com.cielo.applibros.data.remote.api.GutendexApiService
import com.cielo.applibros.data.repository.BookRepositoryImpl
import com.cielo.applibros.domain.usecase.AddToReadUseCase
import com.cielo.applibros.domain.usecase.GetBooksToReadUseCase
import com.cielo.applibros.domain.usecase.GetBooksUseCase
import com.cielo.applibros.domain.usecase.GetCurrentlyReadingUseCase
import com.cielo.applibros.domain.usecase.GetFinishedBooksUseCase
import com.cielo.applibros.domain.usecase.GetReadBooksUseCase
import com.cielo.applibros.domain.usecase.GetUserStatsUseCase
import com.cielo.applibros.domain.usecase.RemoveFromReadUseCase
import com.cielo.applibros.domain.usecase.ToggleFavoriteUseCase
import com.cielo.applibros.domain.usecase.UpdateBookRatingUseCase
import com.cielo.applibros.domain.usecase.UpdateBookReviewUseCase
import com.cielo.applibros.domain.usecase.UpdateBookStatusUseCase
import com.cielo.applibros.presentation.adapter.BookAdapter
import com.cielo.applibros.presentation.dialogs.SearchDialogFragment
import com.cielo.applibros.presentation.fragments.FinishedFragment
import com.cielo.applibros.presentation.fragments.ProfileFragment
import com.cielo.applibros.presentation.fragments.ReadingFragment
import com.cielo.applibros.presentation.fragments.ToReadFragment
import com.cielo.applibros.presentation.viewmodel.BookViewModelUpdated
import com.cielo.applibros.presentation.viewmodel.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var searchFab: FloatingActionButton

    // ViewModels (en un proyecto real usarías ViewModelProvider o Hilt)
    lateinit var bookViewModel: BookViewModelUpdated
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDependencies()
        setupViews()
        setupBottomNavigation()
        setupFab()

        // Cargar el fragmento inicial
        if (savedInstanceState == null) {
            loadFragment(ToReadFragment())
            bottomNavigation.selectedItemId = R.id.nav_to_read
        }
    }

    private fun setupDependencies() {
        // Configurar OkHttpClient
        val okHttpClient = NetworkHelper.createUnsafeOkHttpClient()

        // Retrofit config
        val retrofit = Retrofit.Builder()
            .baseUrl(GutendexApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(GutendexApiService::class.java)
        val database = AppDatabase.getDatabase(this)
        val bookDao = database.bookDao()

        // Crear repository y los casos de uso
        val repository = BookRepositoryImpl(apiService, bookDao)

        // Use cases existentes
        val getBooksUseCase = GetBooksUseCase(repository)
        val getReadBooksUseCase = GetReadBooksUseCase(repository)
        val addToReadUseCase = AddToReadUseCase(repository)
        val removeFromReadUseCase = RemoveFromReadUseCase(repository)
        val updateBookRatingUseCase = UpdateBookRatingUseCase(repository)

        // Nuevos use cases
        val updateBookStatusUseCase = UpdateBookStatusUseCase(repository)
        val updateBookReviewUseCase = UpdateBookReviewUseCase(repository)
        val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
        val getBooksToReadUseCase = GetBooksToReadUseCase(repository)
        val getCurrentlyReadingUseCase = GetCurrentlyReadingUseCase(repository)
        val getFinishedBooksUseCase = GetFinishedBooksUseCase(repository)
        val getUserStatsUseCase = GetUserStatsUseCase(repository)

        // ViewModels
        bookViewModel = BookViewModelUpdated(
            getBooksUseCase,
            getReadBooksUseCase,
            addToReadUseCase,
            removeFromReadUseCase,
            updateBookRatingUseCase,
            updateBookStatusUseCase,
            updateBookReviewUseCase,
            toggleFavoriteUseCase,
            getBooksToReadUseCase,
            getCurrentlyReadingUseCase,
            getFinishedBooksUseCase
        )

        profileViewModel = ProfileViewModel(getUserStatsUseCase)
    }

    private fun setupViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        searchFab = findViewById(R.id.fab_search)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_to_read -> {
                    loadFragment(ToReadFragment())
                    true
                }
                R.id.nav_reading -> {
                    loadFragment(ReadingFragment())
                    true
                }
                R.id.nav_finished -> {
                    loadFragment(FinishedFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFab() {
        searchFab.setOnClickListener {
            val searchDialog = SearchDialogFragment()
            searchDialog.show(supportFragmentManager, "search_dialog")
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Métodos para compartir ViewModels con fragments
    fun getBookViewModel(): BookViewModelUpdated = bookViewModel
    fun getProfileViewModel(): ProfileViewModel = profileViewModel
}