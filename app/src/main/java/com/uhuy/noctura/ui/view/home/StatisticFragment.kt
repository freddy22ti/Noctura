package com.uhuy.noctura.ui.view.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.uhuy.noctura.data.firebase.FirebaseAuthService
import com.uhuy.noctura.data.network.RetrofitInstance
import com.uhuy.noctura.data.repository.SleepRepository
import com.uhuy.noctura.databinding.FragmentStatisticBinding
import com.uhuy.noctura.ui.adapter.StatisticListAdapter
import com.uhuy.noctura.ui.viewmodel.SleepDataViewModel
import com.uhuy.noctura.utils.Resource
import com.uhuy.noctura.utils.ViewModelFactory

class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StatisticListAdapter

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
        // Inflate the layout for this fragment
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)

        adapter = StatisticListAdapter(emptyList()) { uuid ->
            // Call ViewModel to delete the item with the provided UUID
            sleepDataViewModel.deleteSleepData(requireContext(), uuid)
        }
        binding.rvSleepDataList.adapter = adapter
        binding.rvSleepDataList.layoutManager = GridLayoutManager(this.context, 2)

        setupListData()

        return binding.root
    }

    private fun setupListData() {
        sleepDataViewModel.getSleepData(requireContext())
        sleepDataViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("Sleep Data", "Mohon Tunggu...")
                    binding.itemLoading.root.visibility = View.VISIBLE
                    binding.itemEmpty.root.visibility = View.GONE
                    binding.itemError.root.visibility = View.GONE
                    binding.rvSleepDataList.visibility = View.GONE
                }

                is Resource.Success -> {
                    Log.d("Sleep Data", "Data berhasil diambil.")
                    binding.itemLoading.root.visibility = View.GONE
                    binding.itemEmpty.root.visibility = View.GONE
                    binding.itemError.root.visibility = View.GONE
                    binding.rvSleepDataList.visibility = View.VISIBLE

                    val menuItems = resource.data!!.items
                    val filteredItems = menuItems.filter { item ->
                        item.userId == FirebaseAuthService().getCurrentUser()?.uid ?: ""
                    }
                    adapter.updateData(filteredItems)
                }

                is Resource.Error -> {
                    Log.d("Sleep Data", "Data gagal diambil.")
                    binding.itemLoading.root.visibility = View.GONE
                    binding.itemEmpty.root.visibility = View.GONE
                    binding.itemError.root.visibility = View.VISIBLE
                    binding.rvSleepDataList.visibility = View.GONE

                    binding.itemError.btnRetry.setOnClickListener {
                        sleepDataViewModel.getSleepData(requireContext(), true)
                    }
                }

                is Resource.Empty -> {
                    Log.d("Sleep Data", "Data tidak ditemukan.")
                    binding.itemLoading.root.visibility = View.GONE
                    binding.itemEmpty.root.visibility = View.VISIBLE
                    binding.itemError.root.visibility = View.GONE
                    binding.rvSleepDataList.visibility = View.GONE
                }

                else -> {}
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}