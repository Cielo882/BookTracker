package com.cielo.applibros.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cielo.applibros.MainActivity
import com.cielo.applibros.R
import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.presentation.adapter.BookStatsAdapter
import com.cielo.applibros.presentation.viewmodel.BookViewModelUpdated
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.util.*

class StatisticsFragment : Fragment() {

    private lateinit var viewModel: BookViewModelUpdated

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

    // Gráfico desplegable
    private lateinit var btnToggleChart: MaterialButton
    private lateinit var cardChart: MaterialCardView
    private lateinit var lineChart: LineChart
    private var isChartVisible = false

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
        setupChartToggle()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        refreshStatistics()
    }

    private fun refreshStatistics() {
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

        // Gráfico
        btnToggleChart = view.findViewById(R.id.btnToggleChart)
        cardChart = view.findViewById(R.id.cardChart)
        lineChart = view.findViewById(R.id.lineChart)
    }

    private fun setupChartToggle() {
        btnToggleChart.setOnClickListener {
            isChartVisible = !isChartVisible

            if (isChartVisible) {
                cardChart.visibility = View.VISIBLE
                btnToggleChart.text = "Ocultar Gráfico"
                btnToggleChart.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_up)

                // Generar gráfico si hay datos
                viewModel.finishedBooks.value?.let { books ->
                    setupMonthlyChart(books)
                }
            } else {
                cardChart.visibility = View.GONE
                btnToggleChart.text = "Ver Gráfico de Progreso"
                btnToggleChart.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down)
            }
        }
    }

    private fun setupMonthlyChart(books: List<Book>) {
        val monthlyData = calculateMonthlyReading(books)

        // Si no hay datos, mostrar mensaje
        if (monthlyData.all { it == 0 }) {
            lineChart.visibility = View.GONE
            cardChart.findViewById<TextView>(R.id.tvNoData)?.visibility = View.VISIBLE
            return
        }

        cardChart.findViewById<TextView>(R.id.tvNoData)?.visibility = View.GONE
        lineChart.visibility = View.VISIBLE

        val entries = monthlyData.mapIndexed { index, count ->
            Entry(index.toFloat(), count.toFloat())
        }

        val dataSet = LineDataSet(entries, "Libros leídos").apply {
            color = ContextCompat.getColor(requireContext(), R.color.brown_dark)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.brown_dark))
            lineWidth = 3f
            circleRadius = 5f
            setDrawCircleHole(false)
            valueTextSize = 11f
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.brown_light)
            fillAlpha = 100
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(true)
        }

        lineChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            setTouchEnabled(true)
            setScaleEnabled(false)
            setPinchZoom(false)

            // Configurar eje X
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = MonthAxisValueFormatter()
                granularity = 1f
                textSize = 11f
                setDrawGridLines(false)
                textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
            }

            // Configurar eje Y izquierdo
            axisLeft.apply {
                granularity = 1f
                axisMinimum = 0f
                textSize = 11f
                textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
            }

            // Deshabilitar eje Y derecho
            axisRight.isEnabled = false

            // Leyenda
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                textSize = 12f
                textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
            }

            animateX(1000)
            invalidate()
        }
    }

    private fun calculateMonthlyReading(books: List<Book>): List<Int> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val monthlyCount = IntArray(12) { 0 }

        books.forEach { book ->
            book.finishDate?.let { finishDate ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = finishDate

                if (calendar.get(Calendar.YEAR) == currentYear) {
                    val month = calendar.get(Calendar.MONTH)
                    monthlyCount[month]++
                }
            }
        }

        return monthlyCount.toList()
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
                Pair(book, if (days < 1) 1 else days)
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

    // Formatter para los meses
    private class MonthAxisValueFormatter : ValueFormatter() {
        private val months = arrayOf(
            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
        )

        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index in months.indices) months[index] else ""
        }
    }
}