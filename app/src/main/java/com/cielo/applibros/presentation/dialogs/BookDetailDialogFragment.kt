package com.cielo.applibros.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.cielo.applibros.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.cielo.applibros.R
import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.domain.model.ReadingStatus
import com.cielo.applibros.presentation.viewmodel.BookViewModelUpdated
import java.text.SimpleDateFormat
import java.util.*

class BookDetailDialogFragment : DialogFragment() {

    private lateinit var viewModel: BookViewModelUpdated
    private var book: Book? = null

    private lateinit var ivCover: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvAuthor: TextView
    private lateinit var tvStatus: TextView
    private lateinit var spinnerStatus: Spinner
    private lateinit var ratingBar: RatingBar
    private lateinit var etReview: TextInputEditText
    private lateinit var cbFavorite: CheckBox
    private lateinit var tvStartDate: TextView
    private lateinit var tvFinishDate: TextView
    private lateinit var btnSave: Button
    private lateinit var btnClose: Button

    companion object {
        private const val ARG_BOOK_ID = "book_id"

        fun newInstance(book: Book): BookDetailDialogFragment {
            val fragment = BookDetailDialogFragment()
            val bundle = Bundle().apply {
                putInt(ARG_BOOK_ID, book.id)
            }
            fragment.arguments = bundle
            // Guardamos el libro temporalmente para mostrarlo
            fragment.book = book
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_book_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener ViewModel desde MainActivity
        viewModel = (activity as MainActivity).getBookViewModel()

        setupViews(view)
        setupStatusSpinner()
        setupListeners()

        // Mostrar datos del libro
        book?.let { populateViews(it) }
    }

    private fun setupViews(view: View) {
        ivCover = view.findViewById(R.id.ivCover)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvAuthor = view.findViewById(R.id.tvAuthor)
        tvStatus = view.findViewById(R.id.tvStatus)
        spinnerStatus = view.findViewById(R.id.spinnerStatus)
        ratingBar = view.findViewById(R.id.ratingBar)
        etReview = view.findViewById(R.id.etReview)
        cbFavorite = view.findViewById(R.id.cbFavorite)
        tvStartDate = view.findViewById(R.id.tvStartDate)
        tvFinishDate = view.findViewById(R.id.tvFinishDate)
        btnSave = view.findViewById(R.id.btnSave)
        btnClose = view.findViewById(R.id.btnClose)
    }

    private fun setupStatusSpinner() {
        val statusOptions = arrayOf("Por leer", "Leyendo", "Terminado")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statusOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            book?.let { saveBookChanges(it) }
        }

        btnClose.setOnClickListener {
            dismiss()
        }

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Mostrar/ocultar rating según el estado
                val shouldShowRating = position == 2 // Terminado
                ratingBar.visibility = if (shouldShowRating) View.VISIBLE else View.GONE
                cbFavorite.visibility = if (shouldShowRating) View.VISIBLE else View.GONE
                etReview.visibility = if (shouldShowRating) View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun populateViews(book: Book) {
        tvTitle.text = book.title
        tvAuthor.text = book.authorsString

        // Cargar imagen
        book.imageUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_book)
                .into(ivCover)
        }

        // Configurar spinner de estado
        val statusPosition = when (book.readingStatus) {
            ReadingStatus.TO_READ -> 0
            ReadingStatus.READING -> 1
            ReadingStatus.FINISHED -> 2
        }
        spinnerStatus.setSelection(statusPosition)

        // Configurar rating
        ratingBar.rating = book.rating?.toFloat() ?: 0f

        // Configurar reseña
        etReview.setText(book.review ?: "")

        // Configurar favorito
        cbFavorite.isChecked = book.isFavorite

        // Mostrar fechas
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        book.startDate?.let { date ->
            tvStartDate.text = "Iniciado: ${dateFormatter.format(Date(date))}"
            tvStartDate.visibility = View.VISIBLE
        }

        book.finishDate?.let { date ->
            tvFinishDate.text = "Terminado: ${dateFormatter.format(Date(date))}"
            tvFinishDate.visibility = View.VISIBLE
        }
    }

    private fun saveBookChanges(book: Book) {
        val newStatus = when (spinnerStatus.selectedItemPosition) {
            0 -> ReadingStatus.TO_READ
            1 -> ReadingStatus.READING
            2 -> ReadingStatus.FINISHED
            else -> ReadingStatus.TO_READ
        }

        // Guardar cambios
        viewModel.updateBookStatus(book.id, newStatus)

        if (newStatus == ReadingStatus.FINISHED) {
            val rating = ratingBar.rating.toInt()
            if (rating > 0) {
                viewModel.updateBookRating(book.id, rating)
            }

            val review = etReview.text.toString().trim()
            if (review.isNotBlank()) {
                viewModel.updateBookReview(book.id, review)
            }

            viewModel.toggleFavorite(book.id, cbFavorite.isChecked)
        }

        dismiss()
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