package com.cielo.applibros.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cielo.applibros.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.cielo.applibros.R

import com.cielo.applibros.presentation.adapter.SearchBookAdapter
import com.cielo.applibros.presentation.viewmodel.BookViewModelUpdated

class SearchDialogFragment : DialogFragment() {

    private lateinit var viewModel: BookViewModelUpdated
    private lateinit var searchAdapter: SearchBookAdapter
    private lateinit var etSearch: TextInputEditText
    private lateinit var btnSearch: Button
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_search, container, false)
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
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchBookAdapter { book, status ->
            // Agregar libro con el estado seleccionado
            val bookWithStatus = book.copy(readingStatus = status)
            viewModel.addToRead(bookWithStatus)
            dismiss()
        }

        rvSearchResults.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.books.observe(viewLifecycleOwner) { books ->
            searchAdapter.submitList(books)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            // Mostrar error si es necesario
            error?.let {
                // Puedes agregar un Toast o Snackbar aquí
            }
        }
    }

    private fun setupListeners() {
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotBlank()) {
                viewModel.searchBooks(query)
            }
        }

        // Búsqueda al presionar Enter en el teclado
        etSearch.setOnEditorActionListener { _, _, _ ->
            val query = etSearch.text.toString().trim()
            if (query.isNotBlank()) {
                viewModel.searchBooks(query)
                true
            } else {
                false
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return dialog
    }
}

