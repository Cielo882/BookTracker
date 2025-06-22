package com.cielo.applibros.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cielo.applibros.R
import com.cielo.applibros.domain.model.Book

class BookAdapter(
    private val onBookClick: (Book) -> Unit,
    private val onReadClick: (Book) -> Unit,
    private val onRatingChanged: (Book, Float) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val authorTextView: TextView = itemView.findViewById(R.id.tvAuthor)
        private val downloadCountTextView: TextView = itemView.findViewById(R.id.tvDownloadCount)
        private val coverImageView: ImageView = itemView.findViewById(R.id.ivCover)
        private val readButton: ImageView = itemView.findViewById(R.id.ivRead)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar) //
        private val ratingText: TextView = itemView.findViewById(R.id.tvRatingText)

        fun bind(book: Book) {
            titleTextView.text = book.title
            authorTextView.text = book.authors.joinToString(", ")
            downloadCountTextView.text = "Descargas: ${book.downloadCount}"

            // Cargar imagen de portada
            book.coverUrl?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .placeholder(R.drawable.ic_book)
                    .into(coverImageView)
            }?: run {
                // Si no hay URL, mostrar placeholder directamente
                coverImageView.setImageResource(R.drawable.ic_book)
            }

            // Configurar botón de Leido
            readButton.setImageResource(
                if (book.isRead) R.drawable.ic_check_filled
                else R.drawable.ic_check_border
            )

            // sistema de rating
            // ========================================
            if (book.isRead) {
                // Solo mostrar rating si fue leido
                ratingBar.visibility = View.VISIBLE
                ratingText.visibility = View.VISIBLE

                // Configurar el rating actual
                ratingBar.rating = book.rating
                ratingText.text = if (book.rating > 0) {
                    String.format("%.1f", book.rating)
                } else {
                    "Sin calificar"
                }

                // Listener para cambios en el rating
                ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
                    if (fromUser) {
                        onRatingChanged(book, rating)
                        ratingText.text = if (rating > 0) {
                            String.format("%.1f", rating)
                        } else {
                            "Sin calificar"
                        }
                    }
                }
            } else {
                // Ocultar rating si no se ha leido
                ratingBar.visibility = View.GONE
                ratingText.visibility = View.GONE
            }


            // Listeners
            itemView.setOnClickListener { onBookClick(book) }
            readButton.setOnClickListener { onReadClick(book) }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}