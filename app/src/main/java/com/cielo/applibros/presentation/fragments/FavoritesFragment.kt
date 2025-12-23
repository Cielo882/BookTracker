package com.cielo.applibros.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cielo.applibros.MainActivity
import com.cielo.applibros.R
import com.cielo.applibros.domain.model.ReadingStatus
import com.cielo.applibros.presentation.adapter.BookAdapter
import com.cielo.applibros.presentation.dialogs.BookDetailDialogFragment
import com.cielo.applibros.presentation.viewmodel.BookViewModelUpdated

class FavoritesFragment : Fragment() {

    private lateinit var viewModel: BookViewModelUpdated
    private lateinit var bookAdapter: BookAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).getBookViewModel()

        setupSwipeRefresh(view)
        setupRecyclerView(view)
        setupObservers()
    }

    private fun setupSwipeRefresh(view: View) {
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.apply {
            setColorSchemeResources(
                R.color.terracota,
                R.color.terracota_profunda,
                R.color.arena_pastel
            )

            setOnRefreshListener {
                view.postDelayed({
                    isRefreshing = false
                }, 800)
            }
        }
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.rvBooks)

        bookAdapter = BookAdapter(
            onBookClick = { book ->
                val dialog = BookDetailDialogFragment.newInstance(book)
                dialog.show(parentFragmentManager, "book_detail")
            },
            onStatusClick = { book ->
                viewModel.updateBookStatus(book.id, ReadingStatus.TO_READ)
            },
            onRatingChanged = { book, rating ->
                viewModel.updateBookRating(book.id, rating.toInt())
            },
            onFavoriteClick = { book ->
                viewModel.toggleFavorite(book.id, !book.isFavorite)
            }
        )

        recyclerView.apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        // ✅ FILTRAR solo los favoritos
        viewModel.finishedBooks.observe(viewLifecycleOwner) { books ->
            swipeRefresh.isRefreshing = false
            val favoriteBooks = books.filter { it.isFavorite }
            bookAdapter.submitList(favoriteBooks)
        }
    }
}