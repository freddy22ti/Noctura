package com.uhuy.noctura.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.uhuy.noctura.databinding.SettingListItemBinding
import com.uhuy.noctura.data.model.SettingItem

class SettingsListAdapter(
    private val context: Context,
    private val settingList: List<SettingItem>
) : BaseAdapter() {

    override fun getCount(): Int = settingList.size

    override fun getItem(position: Int): Any = settingList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: SettingListItemBinding

        if (convertView == null) {
            binding = SettingListItemBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as SettingListItemBinding
        }

        // Bind data to the views
        val item = settingList[position]
        binding.ivLogo.setImageResource(item.logo)
        binding.tvTitle.text = item.title
        binding.tvDescription.text = item.description

        return binding.root
    }
}