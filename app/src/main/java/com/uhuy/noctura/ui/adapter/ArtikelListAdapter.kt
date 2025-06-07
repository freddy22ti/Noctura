package com.uhuy.noctura.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uhuy.noctura.data.model.News
import com.uhuy.noctura.databinding.HomeArtikelItemBinding

class ArtikelListAdapter(
    private var items: List<News>,
    private val context: Context
) : RecyclerView.Adapter<ArtikelListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: HomeArtikelItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HomeArtikelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvTitle.text = item.title

        // Using Picasso for image loading
        Picasso.get()
            .load(item.urlToImage)
            .resize(512, 0) // Adjust the image size as needed
            .centerCrop()
            .into(holder.binding.ivGambar)

        holder.binding.btnNavigasi.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
            context.startActivity(browserIntent)
        }
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newArticles: List<News>) {
        // Filter out articles with null or empty image URLs
        items = newArticles.filter { !it.urlToImage.isNullOrEmpty() }

        // Notify the adapter that the data has changed
        notifyDataSetChanged()
    }
}