package com.uhuy.noctura.ui.view.home.home

import AutoSliderAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.uhuy.noctura.R
import com.uhuy.noctura.data.model.News
import com.uhuy.noctura.data.network.RetrofitInstance
import com.uhuy.noctura.data.repository.NewsRepository
import com.uhuy.noctura.databinding.FragmentHomeBinding
import com.uhuy.noctura.ui.viewmodel.NewsViewModel
import com.uhuy.noctura.ui.viewmodel.NewsViewModelFactory
import kotlin.reflect.typeOf

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ViewModel initialization
    private val newsViewModel: NewsViewModel by lazy {
        val repository = NewsRepository(RetrofitInstance.getNewsApi())
        val factory = NewsViewModelFactory(repository)
        ViewModelProvider(this, factory)[NewsViewModel::class.java]
    }

    private lateinit var artikelAdapter: ArtikelListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupArtikelTerbaru()
        setupObservers()
        setupAutoSlider()

        return binding.root
    }

    private fun setupObservers() {
        // Start fetching news
        newsViewModel.fetchNews()

        // Observe the news data
        newsViewModel.newsData.observe(viewLifecycleOwner) { articles ->
            if (!articles.isNullOrEmpty()) {
                // Filter out articles with "[removed]"
                val filteredArticles = articles.filter { article ->
                    article.title != ("[Removed]") && article.urlToImage != null
                }


                // Update adapter with filtered data
                artikelAdapter.updateData(filteredArticles)
            } else {
                showEmptyState()
            }
        }

        // Observe loading state
        newsViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoadingSpinner()
            } else {
                hideLoadingSpinner()
            }
        }
    }

    private fun setupAutoSlider() {
        val images = listOf(
            "https://img.freepik.com/free-vector/woman-having-problems-with-sleeping-illustrated_23-2148677728.jpg",
            "https://img.freepik.com/free-vector/woman-having-problems-with-sleeping-illustrated_23-2148677728.jpg",
            "https://img.freepik.com/free-vector/woman-having-problems-with-sleeping-illustrated_23-2148677728.jpg"
        )

        binding.autoSlider.adapter = AutoSliderAdapter(images, binding.autoSlider)
        binding.dotsIndicator.attachTo(binding.autoSlider)
    }

    private fun setupArtikelTerbaru() {
        val gridLayoutManager = GridLayoutManager(
            requireContext(),
            3,
            GridLayoutManager.HORIZONTAL,
            false
        )

        artikelAdapter = ArtikelListAdapter(emptyList(), requireContext())

        binding.rvArtikelTerbaru.apply {
            adapter = artikelAdapter
            layoutManager = gridLayoutManager
        }
    }

    private fun showLoadingSpinner() {
//        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingSpinner() {
//        binding.progressBar.visibility = View.GONE
    }

    private fun showEmptyState() {
//        binding.emptyStateTextView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
