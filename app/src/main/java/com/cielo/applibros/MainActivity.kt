package com.cielo.applibros

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.cielo.applibros.data.local.database.AppDatabase
import com.cielo.applibros.data.remote.api.GutendexApiService
import com.cielo.applibros.data.repository.BookRepositoryImpl
import com.cielo.applibros.domain.model.ReadingStatus
import com.cielo.applibros.domain.usecase.*
import com.cielo.applibros.presentation.fragments.*
import com.cielo.applibros.presentation.viewmodel.BookViewModelUpdated
import com.cielo.applibros.presentation.viewmodel.ProfileViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Views
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var toolbar: Toolbar

    // ViewModels
    private lateinit var bookViewModel: BookViewModelUpdated
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDependencies()
        setupViews()
        setupToolbar()
        setupDrawer()
        setupBottomNavigation()

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
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
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
                R.id.nav_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.nav_finished -> {
                    loadFragment(FinishedFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Implementación del NavigationView.OnNavigationItemSelectedListener
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                loadFragment(ProfileFragment())
            }
            R.id.nav_statistics -> {
                loadFragment(ProfileFragment()) // Por ahora usa ProfileFragment
            }
            R.id.nav_favorites -> {
                // Cargar fragment de favoritos o filtrar libros favoritos
                loadFragment(FinishedFragment())
            }
            R.id.nav_settings -> {
                // TODO: Implementar pantalla de configuración
                showComingSoon("Ajustes")
            }
            R.id.nav_about -> {
                showAboutDialog()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Métodos públicos para compartir ViewModels con fragments
    fun getBookViewModel(): BookViewModelUpdated = bookViewModel
    fun getProfileViewModel(): ProfileViewModel = profileViewModel

    // Método para navegar a una sección específica desde otros fragments
    fun navigateToStatus(status: ReadingStatus) {
        val itemId = when (status) {
            ReadingStatus.TO_READ -> R.id.nav_to_read
            ReadingStatus.READING -> R.id.nav_reading
            ReadingStatus.FINISHED -> R.id.nav_finished
        }
        bottomNavigation.selectedItemId = itemId
    }

    private fun showComingSoon(feature: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Próximamente")
            .setMessage("La función '$feature' estará disponible en una próxima actualización.")
            .setPositiveButton("Entendido", null)
            .show()
    }

    private fun showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Acerca de BookTracker")
            .setMessage(
                "BookTracker v2.0\n\n" +
                        "Tu biblioteca personal para organizar y rastrear tus lecturas.\n\n" +
                        "Desarrollado con ❤️ usando Clean Architecture y Kotlin"
            )
            .setPositiveButton("Cerrar", null)
            .show()
    }
}