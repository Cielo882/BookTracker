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
import com.cielo.applibros.presentation.adapter.BookAdapter
import com.cielo.applibros.presentation.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var currentlyReadingAdapter: BookAdapter
    private lateinit var favoritesAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener ViewModel desde MainActivity
        viewModel = (activity as MainActivity).getProfileViewModel()

        setupViews(view)
        setupObservers()

        viewModel.loadUserStats()
    }

    private fun setupViews(view: View) {
        // Configurar adapters para listas horizontales
        currentlyReadingAdapter = BookAdapter(
            onBookClick = { /* Manejar clic */ },
            onStatusClick = { /* Manejar clic */ },
            onRatingChanged = { _, _ -> /* No aplicable aquí */ }
        )

        favoritesAdapter = BookAdapter(
            onBookClick = { /* Manejar clic */ },
            onStatusClick = { /* Manejar clic */ },
            onRatingChanged = { _, _ -> /* No aplicable aquí */ }
        )

        // RecyclerView para libros actuales (horizontal)
        view.findViewById<RecyclerView>(R.id.rvCurrentlyReading).apply {
            adapter = currentlyReadingAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // RecyclerView para favoritos (horizontal)
        view.findViewById<RecyclerView>(R.id.rvFavorites).apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupObservers() {
        viewModel.userStats.observe(viewLifecycleOwner) { stats ->
            view?.let { v ->
                v.findViewById<TextView>(R.id.tvTotalRead).text = stats.totalBooksRead.toString()
                v.findViewById<TextView>(R.id.tvTotalToRead).text = stats.totalBooksToRead.toString()
                v.findViewById<TextView>(R.id.tvAverageRating).text =
                    String.format("%.1f", stats.averageRating)

                currentlyReadingAdapter.submitList(stats.currentlyReading)
                favoritesAdapter.submitList(stats.favoriteBooks)
            }
        }
    }
}