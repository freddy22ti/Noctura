package com.uhuy.noctura.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uhuy.noctura.data.model.SleepItem
import com.uhuy.noctura.databinding.StatisticHistoryItemBinding

class StatisticListAdapter(
    private var mList: List<SleepItem>,
    private val onDeleteClick: (String) -> Unit // Callback for delete button
) : RecyclerView.Adapter<StatisticListAdapter.ViewHolder>() {

    class ViewHolder(val binding: StatisticHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StatisticHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]

        holder.binding.tvDate.text = "Tanggal: " + item.date
        holder.binding.tvSleepTime.text = "Waktu Tidur: " + item.sleepTime
        holder.binding.tvWakeupTime.text = "Waktu Bangun: " + item.wakeTime
        holder.binding.tvSleepDuration.text = "Durasi Tidur: " + item.sleepTime
        holder.binding.tvAwakenings.text = "Jumlah Terbangun: " + item.awakenings
        holder.binding.tvAlcohol.text = "Jumlah Alkohol Yang Dikonsumsi: " + item.alcoholConsumption
        holder.binding.tvCaffeine.text =
            "Jumlah Kafein Yang Dikonsumsi: " + item.caffeineConsumption
        holder.binding.tvSleepQuality.text = "Kualitas Tidur: " + item.sleepQuality

        // Set delete button click listener
        holder.binding.btnRemoveData.setOnClickListener {
            onDeleteClick(item._uuid) // Pass the item's UUID to the callback
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateData(sleepDataItem: List<SleepItem>) {
        mList = sleepDataItem
        notifyDataSetChanged()
    }
}