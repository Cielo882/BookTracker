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
import com.cielo.applibros.presentation.adapter.CompactBookAdapter
import com.cielo.applibros.presentation.dialogs.BookDetailDialogFragment
import com.cielo.applibros.presentation.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var currentlyReadingAdapter: CompactBookAdapter
    private lateinit var favoritesAdapter: CompactBookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).getProfileViewModel()

        applyOverlay(view)

        setupViews(view)
        setupObservers()

        viewModel.loadUserStats()
    }

    private fun applyOverlay(view: View) {
        val overlay = view.findViewById<View>(R.id.profileOverlay)

        val nightMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK

        if (nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            // Modo oscuro → overlay negro suave
            overlay.setBackgroundColor(
                android.graphics.Color.parseColor("#CC000000")
            )
        } else {
            // Modo claro → overlay blanco suave
            overlay.setBackgroundColor(
                android.graphics.Color.parseColor("#E6FFFFFF")
            )
        }
    }

    private fun setupViews(view: View) {
        // Crear adaptadores compactos
        currentlyReadingAdapter = CompactBookAdapter { book ->
            val dialog = BookDetailDialogFragment.newInstance(book)
            dialog.show(parentFragmentManager, "book_detail")
        }

        favoritesAdapter = CompactBookAdapter { book ->
            val dialog = BookDetailDialogFragment.newInstance(book)
            dialog.show(parentFragmentManager, "book_detail")
        }

        // Configurar RecyclerView para "Leyendo Actualmente"
        view.findViewById<RecyclerView>(R.id.rvCurrentlyReading).apply {
            adapter = currentlyReadingAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
        }

        // Configurar RecyclerView para "Mis Favoritos"
        view.findViewById<RecyclerView>(R.id.rvFavorites).apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
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