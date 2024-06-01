package com.example.playtronic

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//SE ENCARGA DE MOSTRAR LA NOTICIA EN EL RECYCLERVIEW
class EventosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val eventosImage: ImageView = view.findViewById(R.id.eventosImage)
    val eventosTitle: TextView = view.findViewById(R.id.eventosTitle)
    val eventosDate: TextView = view.findViewById(R.id.eventosDate)
}