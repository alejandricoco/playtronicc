package com.example.playtronic

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*

class NewsAdapter(private val newsList: List<News>) : RecyclerView.Adapter<NewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        // Aquí puedes usar una biblioteca como Glide o Picasso para cargar la imagen desde la URL
        Glide.with(holder.newsImage.context).load(news.image).into(holder.newsImage)
        holder.newsTitle.text = news.title

        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        holder.newsDate.text = currentDate
    }

    override fun getItemCount() = newsList.size
}