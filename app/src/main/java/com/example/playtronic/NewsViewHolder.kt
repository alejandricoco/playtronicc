package com.example.playtronic

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//SE ENCARGA DE MOSTRAR LA NOTICIA EN EL RECYCLERVIEW
class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val newsImage: ImageView = view.findViewById(R.id.newsImage)
    val newsTitle: TextView = view.findViewById(R.id.newsTitle)
    val newsDate: TextView = view.findViewById(R.id.newsDate)
}