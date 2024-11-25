package com.uhuy.noctura.ui.view.home.home

import android.content.Context
import android.content.Intent
import android.net.Uri
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
        // Inflate the binding
        val binding = HomeArtikelItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Use the binding to set data
        holder.binding.tvTitle.text = item.title
        Picasso.get()
            .load(item.urlToImage)
            .resize(512, 0)
            .into(holder.binding.ivGambar)

        holder.binding.btnNavigasi.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
            context.startActivity(browserIntent)
        }

    }

    override fun getItemCount() = items.size

    fun updateData(newArticles: List<News>) {
        items = newArticles
        notifyDataSetChanged()
    }
}