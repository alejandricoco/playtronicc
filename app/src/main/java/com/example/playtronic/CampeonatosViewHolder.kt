package com.example.playtronic

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//SE ENCARGA DE MOSTRAR LA NOTICIA EN EL RECYCLERVIEW
class CampeonatosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val campeonatosImage: ImageView = view.findViewById(R.id.campeonatosImage)
    val campeonatosTitle: TextView = view.findViewById(R.id.campeonatosTitle)
    val campeonatosDate: TextView = view.findViewById(R.id.campeonatosDate)
}