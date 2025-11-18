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
import java.util.*

class StatisticsFragment : Fragment() {

    private lateinit var viewModel: BookViewModelUpdated
    private lateinit var bookStatsAdapter: BookStatsAdapter

    // Estadísticas generales
    private lateinit var tvTotalBooksRead: TextView
    private lateinit var tvTotalBooksReading: TextView
    private lateinit var tvTotalBooksToRead: TextView
    private lateinit var tvAverageRating: TextView
    private lateinit var tvAverageReadingDays: TextView
    private lateinit var tvFastestBook: TextView
    private lateinit var tvSlowestBook: TextView

    // Estadísticas del año actual
    private lateinit var tvBooksThisYear: TextView
    private lateinit var tvBooksThisMonth: TextView
    private lateinit var tvPagesThisYear: TextView // Si implementas páginas

    // Lista de libros con estadísticas
    private lateinit var rvBookStats: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).getBookViewModel()

        setupViews(view)
        setupRecyclerView()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        // Forzar recarga de datos cuando se vuelve al fragment
        refreshStatistics()
    }

    private fun refreshStatistics() {
        // Las LiveData se actualizarán automáticamente
        // y triggearán los observadores
        viewModel.finishedBooks.value?.let { books ->
            calculateStatistics(books)
        }
    }

    private fun setupViews(view: View) {
        // Estadísticas generales
        tvTotalBooksRead = view.findViewById(R.id.tvTotalBooksRead)
        tvTotalBooksReading = view.findViewById(R.id.tvTotalBooksReading)
        tvTotalBooksToRead = view.findViewById(R.id.tvTotalBooksToRead)
        tvAverageRating = view.findViewById(R.id.tvAverageRating)
        tvAverageReadingDays = view.findViewById(R.id.tvAverageReadingDays)
        tvFastestBook = view.findViewById(R.id.tvFastestBook)
        tvSlowestBook = view.findViewById(R.id.tvSlowestBook)

        // Estadísticas del año
        tvBooksThisYear = view.findViewById(R.id.tvBooksThisYear)
        tvBooksThisMonth = view.findViewById(R.id.tvBooksThisMonth)

        // RecyclerView
        rvBookStats = view.findViewById(R.id.rvBookStats)
    }

    private fun setupRecyclerView() {
        bookStatsAdapter = BookStatsAdapter()
        rvBookStats.apply {
            adapter = bookStatsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeData() {
        // Observar libros terminados
        viewModel.finishedBooks.observe(viewLifecycleOwner) { finishedBooks ->
            calculateStatistics(finishedBooks)
        }

        // Observar libros leyendo
        viewModel.currentlyReading.observe(viewLifecycleOwner) { readingBooks ->
            tvTotalBooksReading.text = readingBooks.size.toString()
        }

        // Observar libros por leer
        viewModel.booksToRead.observe(viewLifecycleOwner) { toReadBooks ->
            tvTotalBooksToRead.text = toReadBooks.size.toString()
        }
    }

    private fun calculateStatistics(books: List<Book>) {
        if (books.isEmpty()) {
            showEmptyState()
            return
        }

        // Total de libros leídos
        tvTotalBooksRead.text = books.size.toString()

        // Promedio de calificaciones
        val booksWithRating = books.filter { it.rating != null && it.rating > 0 }
        val averageRating = if (booksWithRating.isNotEmpty()) {
            booksWithRating.map { it.rating!! }.average()
        } else 0.0
        tvAverageRating.text = String.format("%.1f", averageRating)

        // Calcular días de lectura
        val booksWithDates = books.filter {
            it.startDate != null && it.finishDate != null
        }

        if (booksWithDates.isNotEmpty()) {
            val readingDays = booksWithDates.map { book ->
                val days = ((book.finishDate!! - book.startDate!!) / (1000 * 60 * 60 * 24)).toInt()
                Pair(book, days)
            }

            // Promedio de días
            val averageDays = readingDays.map { it.second }.average()
            tvAverageReadingDays.text = "${averageDays.toInt()} días"

            // Libro más rápido
            val fastest = readingDays.minByOrNull { it.second }
            tvFastestBook.text = fastest?.let {
                "${it.first.title} (${it.second} días)"
            } ?: "N/A"

            // Libro más lento
            val slowest = readingDays.maxByOrNull { it.second }
            tvSlowestBook.text = slowest?.let {
                "${it.first.title} (${it.second} días)"
            } ?: "N/A"

            // Mostrar lista ordenada por días
            bookStatsAdapter.submitList(readingDays.sortedBy { it.second })
        } else {
            tvAverageReadingDays.text = "N/A"
            tvFastestBook.text = "N/A"
            tvSlowestBook.text = "N/A"
        }

        // Estadísticas del año actual
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val booksThisYear = books.filter { book ->
            book.finishDate?.let { finishDate ->
                val bookCalendar = Calendar.getInstance()
                bookCalendar.timeInMillis = finishDate
                bookCalendar.get(Calendar.YEAR) == currentYear
            } ?: false
        }

        tvBooksThisYear.text = booksThisYear.size.toString()

        val booksThisMonth = booksThisYear.filter { book ->
            book.finishDate?.let { finishDate ->
                val bookCalendar = Calendar.getInstance()
                bookCalendar.timeInMillis = finishDate
                bookCalendar.get(Calendar.MONTH) == currentMonth
            } ?: false
        }

        tvBooksThisMonth.text = booksThisMonth.size.toString()
    }

    private fun showEmptyState() {
        tvTotalBooksRead.text = "0"
        tvAverageRating.text = "0.0"
        tvAverageReadingDays.text = "N/A"
        tvFastestBook.text = "N/A"
        tvSlowestBook.text = "N/A"
        tvBooksThisYear.text = "0"
        tvBooksThisMonth.text = "0"
    }
}