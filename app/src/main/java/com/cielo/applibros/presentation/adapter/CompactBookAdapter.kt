package com.cielo.applibros.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cielo.applibros.R
import com.cielo.applibros.domain.model.Book

class CompactBookAdapter(
    private val onBookClick: (Book) -> Unit
) : ListAdapter<Book, CompactBookAdapter.CompactBookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompactBookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_compact, parent, false)
        return CompactBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompactBookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CompactBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)

        fun bind(book: Book) {
            tvTitle.text = book.title

            book.imageUrl?.let { url ->
                Glide.with(itemView.context)
                    .load(url)
                    .placeholder(R.drawable.ic_book)
                    .into(ivCover)
            } ?: run {
                ivCover.setImageResource(R.drawable.ic_book)
            }

            itemView.setOnClickListener { onBookClick(book) }
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