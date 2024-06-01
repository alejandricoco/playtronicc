package com.example.playtronic

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playtronic.fragments.fragmentsMenu.GlobalFragment

class NewsAdapter(val newsList: List<News>) : RecyclerView.Adapter<NewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        // Aquí puedes usar una biblioteca como Glide o Picasso para cargar la imagen desde la URL
        Glide.with(holder.newsImage.context).load(news.image).into(holder.newsImage)
        holder.newsTitle.text = news.title

        holder.newsDate.text = news.date
    }

    override fun getItemCount() = newsList.size


    fun openGlobalFragment(context: Context, news: News) {
        val fragment = GlobalFragment() // Reemplaza esto con tu fragmento global
        val bundle = Bundle()
        bundle.putString("image", news.image)
        bundle.putString("title", news.title)
        bundle.putString("date", news.date)
        bundle.putString("cuerpo", news.cuerpo)
        fragment.arguments = bundle
        // Aquí puedes realizar la transacción de fragmentos para abrir tu fragmento global
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit() // Reemplaza 'container' con el ID de tu contenedor de fragmentos
    }

}