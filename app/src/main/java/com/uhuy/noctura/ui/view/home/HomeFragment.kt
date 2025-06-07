package com.uhuy.noctura.ui.view.home

import com.uhuy.noctura.ui.adapter.AutoSliderAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.uhuy.noctura.R
import com.uhuy.noctura.data.firebase.FirebaseAuthService
import com.uhuy.noctura.data.model.SleepItem
import com.uhuy.noctura.data.network.RetrofitInstance
import com.uhuy.noctura.data.repository.NewsRepository
import com.uhuy.noctura.data.repository.SleepRepository
import com.uhuy.noctura.databinding.FragmentHomeBinding
import com.uhuy.noctura.ui.adapter.ArtikelListAdapter
import com.uhuy.noctura.ui.viewmodel.NewsViewModel
import com.uhuy.noctura.ui.viewmodel.SleepDataViewModel
import com.uhuy.noctura.utils.Resource
import com.uhuy.noctura.utils.ViewModelFactory
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var artikelAdapter: ArtikelListAdapter

    // ViewModel initialization
    private val newsViewModel: NewsViewModel by activityViewModels {
        ViewModelFactory(NewsViewModel::class.java) {
            val repository = NewsRepository(RetrofitInstance.getNewsApi())
            NewsViewModel(repository)
        }
    }

    private val sleepDataViewModel: SleepDataViewModel by activityViewModels {
        ViewModelFactory(SleepDataViewModel::class.java) {
            val repository = SleepRepository(RetrofitInstance.getCrudApi())
            SleepDataViewModel(repository)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize the adapter with an empty list
        artikelAdapter = ArtikelListAdapter(emptyList(), requireContext())

        // Setup fragment components
        setupArtikelTerbaru()
        setupLatestSleepQuality()
        setupAutoSlider()

        return binding.root
    }

    private fun setupLatestSleepQuality() {
        sleepDataViewModel.getSleepData(requireContext())
        sleepDataViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.tvHasilPrediksi.text = "-"
                }

                is Resource.Success -> {
                    val items = resource.data!!.items
                    val filteredItems = items.filter { item ->
                        item.userId == FirebaseAuthService().getCurrentUser()?.uid ?: ""
                    }
                    val firstItem = filteredItems.firstOrNull()
                    val prediction: Float = firstItem?.sleepQuality ?: -1.0f
                    Log.d("HomeFragment", "The first item is $prediction")
                    val predictionRounded = String.format(Locale.US, "%.2f", prediction).toFloat()
                    val statusMsg: String = when (predictionRounded) {
                        in 0.0f..0.69f -> {
                            "Kurang Baik"
                        }
                        in 0.7f..0.79f -> {
                            "Baik"
                        }
                        in 0.8f..1.0f -> {
                            "Sangat Baik"
                        }
                        else -> {
                            "-"
                        }
                    }
                    binding.tvHasilPrediksi.text = statusMsg
                }

                is Resource.Error -> {
                    binding.tvHasilPrediksi.text = "-"
                }

                is Resource.Empty -> {
                    binding.tvHasilPrediksi.text = "-"
                }

                else -> {}
            }
        }
    }

    private fun setupAutoSlider() {
        val images = listOf(
            "https://img.freepik.com/free-vector/woman-having-problems-with-sleeping-illustrated_23-2148677728.jpg",
            "https://img.freepik.com/free-vector/insomnia-illustration-concept_23-2148662910.jpg?t=st=1736084600~exp=1736088200~hmac=d2538712e3110743d174886a3f7281a9d714fcda6e52c9667f38c186e6f291e4&w=900",
            "https://img.freepik.com/free-vector/insomnia-concept-illustration_23-2148659569.jpg?t=st=1736086382~exp=1736089982~hmac=8cd189c8add7117be45fce74382de174266559be3c0578a10f16cbf884411b65&w=1060",
            "https://img.freepik.com/free-vector/insomnia-concept-illustration_23-2148665006.jpg?t=st=1736086384~exp=1736089984~hmac=5a0c381d03da857a7d21404f20b8b498346fe2dd40b7e7b6bc2107b8c899e80a&w=900",
            "https://img.freepik.com/free-vector/insomnia-causes-illustration-concept_23-2148647086.jpg?t=st=1736086386~exp=1736089986~hmac=c587052b54dad9716574d050da014af9f4e11b9c0131dfc9555eae01522a0135&w=1380",
            "https://img.freepik.com/free-vector/insomnia-concept_23-2148653636.jpg?t=st=1736086387~exp=1736089987~hmac=319c969c54b08f0b79a9d928f0bf53f92a041d04351745714945f5c38d8ec29a&w=900",
        )

        binding.autoSlider.adapter = AutoSliderAdapter(images, binding.autoSlider)
        binding.dotsIndicator.attachTo(binding.autoSlider)
    }

    private fun setupArtikelTerbaru() {
        // Set up GridLayoutManager for RecyclerView
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.HORIZONTAL, false)
        binding.rvArtikelTerbaru.layoutManager = gridLayoutManager
        binding.rvArtikelTerbaru.adapter = artikelAdapter

        binding.itemEmpty.tvEmptyMessage.text = getString(R.string.no_articles)

        // Fetch the news data
        newsViewModel.getNewsData(requireContext())
        newsViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.itemLoading.root.visibility = View.VISIBLE
                    binding.rvArtikelTerbaru.visibility = View.GONE
                    binding.itemError.root.visibility = View.GONE
                    binding.itemEmpty.root.visibility = View.GONE
                }

                is Resource.Success -> {
                    // Check if articles are present in the response
                    val newsItems = resource.data?.articles ?: emptyList()
                    if (newsItems.isNotEmpty()) {
                        binding.itemLoading.root.visibility = View.GONE
                        binding.rvArtikelTerbaru.visibility = View.VISIBLE
                        binding.itemError.root.visibility = View.GONE
                        binding.itemEmpty.root.visibility = View.GONE
                        artikelAdapter.updateData(newsItems)
                    } else {
                        binding.itemLoading.root.visibility = View.GONE
                        binding.rvArtikelTerbaru.visibility = View.GONE
                        binding.itemError.root.visibility = View.GONE
                        binding.itemEmpty.root.visibility = View.VISIBLE
                    }
                }

                is Resource.Error -> {
                    binding.itemLoading.root.visibility = View.GONE
                    binding.rvArtikelTerbaru.visibility = View.GONE
                    binding.itemError.root.visibility = View.VISIBLE
                    binding.itemEmpty.root.visibility = View.GONE

                    binding.itemError.btnRetry.setOnClickListener {
                        newsViewModel.getNewsData(requireContext(), true)
                    }
                }

                is Resource.Empty -> {
                    binding.itemLoading.root.visibility = View.GONE
                    binding.rvArtikelTerbaru.visibility = View.GONE
                    binding.itemError.root.visibility = View.GONE
                    binding.itemEmpty.root.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}