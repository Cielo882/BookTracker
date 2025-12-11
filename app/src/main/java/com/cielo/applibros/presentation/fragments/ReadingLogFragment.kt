package com.cielo.applibros.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cielo.applibros.MainActivity
import com.cielo.applibros.R
import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.presentation.adapter.BookStatsAdapter
import com.cielo.applibros.presentation.viewmodel.BookViewModelUpdated
import java.util.Calendar



class ReadingLogFragment : Fragment() {

    private lateinit var viewModel: BookViewModelUpdated
    private lateinit var bookStatsAdapter: BookStatsAdapter

    // Views
    private lateinit var rvReadingLog: RecyclerView
    private lateinit var tvTotalBooks: TextView
    private lateinit var tvEmptyState: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reading_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).getBookViewModel()

        setupViews(view)
        setupRecyclerView()
        observeData()
    }

    private fun setupViews(view: View) {
        rvReadingLog = view.findViewById(R.id.rvReadingLog)
        tvTotalBooks = view.findViewById(R.id.tvTotalBooks)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
    }

    private fun setupRecyclerView() {
        bookStatsAdapter = BookStatsAdapter()
        rvReadingLog.apply {
            adapter = bookStatsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun observeData() {
        viewModel.finishedBooks.observe(viewLifecycleOwner) { finishedBooks ->
            if (finishedBooks.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                displayReadingLog(finishedBooks)
            }
        }
    }

    private fun displayReadingLog(books: List<Book>) {
        // Filtrar libros con fechas válidas
        val booksWithDates = books.filter {
            it.startDate != null && it.finishDate != null
        }

        if (booksWithDates.isEmpty()) {
            showEmptyState()
            return
        }

        // Calcular días de lectura para cada libro
        val readingData = booksWithDates.map { book ->
            val days = calculateReadingDays(book.startDate!!, book.finishDate!!)
            Pair(book, days)
        }

        // Ordenar por fecha de finalización (más reciente primero)
        val sortedData = readingData.sortedByDescending { it.first.finishDate }

        // Actualizar UI
        tvTotalBooks.text = "Total de registros: ${sortedData.size}"
        bookStatsAdapter.submitList(sortedData)
    }

    private fun calculateReadingDays(startDate: Long, finishDate: Long): Int {
        val days = ((finishDate - startDate) / (1000 * 60 * 60 * 24)).toInt()
        return if (days < 1) 1 else days // Mínimo 1 día
    }

    private fun showEmptyState() {
        rvReadingLog.visibility = View.GONE
        tvEmptyState.visibility = View.VISIBLE
        tvTotalBooks.text = "Total de registros: 0"
    }

    private fun hideEmptyState() {
        rvReadingLog.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE
    }
}