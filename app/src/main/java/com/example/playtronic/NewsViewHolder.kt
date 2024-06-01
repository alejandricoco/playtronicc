package com.example.playtronic

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//SE ENCARGA DE MOSTRAR LA NOTICIA EN EL RECYCLERVIEW
class NewsViewHolder(view: View, private val adapter: NewsAdapter) : RecyclerView.ViewHolder(view) {
    val newsImage: ImageView = view.findViewById(R.id.newsImage)
    val newsTitle: TextView = view.findViewById(R.id.newsTitle)
    val newsDate: TextView = view.findViewById(R.id.newsDate)


    init {
        itemView.setOnClickListener {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) { // Comprueba si el elemento aún existe
                val clickedItem = adapter.newsList[position]
                // Aquí puedes llamar a tu método para abrir el fragmento global y pasar los datos del elemento clicado
                adapter.openGlobalFragment(itemView.context, clickedItem)
            }
        }
    }

}