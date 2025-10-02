package com.cielo.applibros.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cielo.applibros.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.cielo.applibros.R
import com.cielo.applibros.domain.model.ReadingStatus
import com.cielo.applibros.presentation.adapter.SearchBookAdapter
import com.cielo.applibros.presentation.viewmodel.BookViewModelUpdated
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SearchFragment : Fragment() {

    private lateinit var viewModel: BookViewModelUpdated
    private lateinit var searchAdapter: SearchBookAdapter

    private lateinit var etSearch: TextInputEditText
    private lateinit var btnSearch: Button
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener ViewModel desde MainActivity
        viewModel = (activity as MainActivity).getBookViewModel()

        setupViews(view)
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupViews(view: View) {
        etSearch = view.findViewById(R.id.etSearch)
        btnSearch = view.findViewById(R.id.btnSearch)
        rvSearchResults = view.findViewById(R.id.rvSearchResults)
        progressBar = view.findViewById(R.id.progressBar)
        emptyState = view.findViewById(R.id.emptyState)
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchBookAdapter { book, status ->
            // Agregar libro con el estado seleccionado
            val bookWithStatus = book.copy(readingStatus = status)
            viewModel.addToRead(bookWithStatus)

            // Mostrar confirmación
            showBookAddedConfirmation(book.title, status)
        }

        rvSearchResults.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.books.observe(viewLifecycleOwner) { books ->
            if (books.isNotEmpty()) {
                // Mostrar resultados
                emptyState.visibility = View.GONE
                rvSearchResults.visibility = View.VISIBLE
                searchAdapter.submitList(books)
            } else {
                // Si la búsqueda se completó pero no hay resultados
                if (viewModel.isLoading.value == false && etSearch.text?.isNotBlank() == true) {
                    showNoResults()
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

            if (isLoading) {
                emptyState.visibility = View.GONE
                rvSearchResults.visibility = View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
            }
        }
    }

    private fun setupListeners() {
        btnSearch.setOnClickListener {
            performSearch()
        }

        // Búsqueda al presionar Enter
        etSearch.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }
    }

    private fun performSearch() {
        val query = etSearch.text.toString().trim()
        if (query.isNotBlank()) {
            viewModel.searchBooks(query)
        } else {
            etSearch.error = "Ingresa un término de búsqueda"
        }
    }

    private fun showNoResults() {
        emptyState.visibility = View.VISIBLE
        rvSearchResults.visibility = View.GONE

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sin resultados")
            .setMessage("No se encontraron libros con ese criterio. Intenta con otro término.")
            .setPositiveButton("Entendido", null)
            .show()
    }

    private fun showError(errorMessage: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(errorMessage)
            .setPositiveButton("Reintentar") { _, _ ->
                performSearch()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showBookAddedConfirmation(bookTitle: String, status: ReadingStatus) {
        val statusText = when (status) {
            ReadingStatus.TO_READ -> "Por Leer"
            ReadingStatus.READING -> "Leyendo"
            ReadingStatus.FINISHED -> "Leídos"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("¡Libro agregado!")
            .setMessage("\"$bookTitle\" fue agregado a $statusText")
            .setPositiveButton("Ver") { _, _ ->
                // Navegar a la sección correspondiente
                (activity as? MainActivity)?.navigateToStatus(status)
            }
            .setNegativeButton("Continuar buscando", null)
            .show()
    }
}