package com.cielo.applibros.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cielo.applibros.R
import com.cielo.applibros.domain.model.Book
import com.cielo.applibros.domain.model.ReadingStatus

class SearchBookAdapter(
    private val onAddBook: (Book, ReadingStatus) -> Unit
) : ListAdapter<Book, SearchBookAdapter.SearchBookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchBookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_book, parent, false)
        return SearchBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchBookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val authorTextView: TextView = itemView.findViewById(R.id.tvAuthor)
        private val subjectsTextView: TextView = itemView.findViewById(R.id.tvSubjects)
        private val coverImageView: ImageView = itemView.findViewById(R.id.ivCover)
        private val btnToRead: Button = itemView.findViewById(R.id.btnToRead)
        private val btnReading: Button = itemView.findViewById(R.id.btnReading)

        fun bind(book: Book) {
            titleTextView.text = book.title
            authorTextView.text = book.authorsString
            subjectsTextView.text = book.subjects.take(3).joinToString(", ")

            // Cargar imagen de portada
            book.imageUrl?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .placeholder(R.drawable.ic_book)
                    .into(coverImageView)
            } ?: run {
                coverImageView.setImageResource(R.drawable.ic_book)
            }

            // Configurar botones
            btnToRead.setOnClickListener {
                onAddBook(book, ReadingStatus.TO_READ)
            }

            btnReading.setOnClickListener {
                onAddBook(book, ReadingStatus.READING)
            }
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