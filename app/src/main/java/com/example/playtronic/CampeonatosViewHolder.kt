package com.example.playtronic

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//SE ENCARGA DE MOSTRAR LA NOTICIA EN EL RECYCLERVIEW
class CampeonatosViewHolder(view: View, private val adapter: CampeonatosAdapter) : RecyclerView.ViewHolder(view) {
    val campeonatosImage: ImageView = view.findViewById(R.id.campeonatosImage)
    val campeonatosTitle: TextView = view.findViewById(R.id.campeonatosTitle)
    val campeonatosDate: TextView = view.findViewById(R.id.campeonatosDate)


    init {
        itemView.setOnClickListener {

            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) { // Comprueba si el elemento aún existe
                val clickedItem = adapter.campeonatosList[position]
                // Aquí puedes llamar a tu método para abrir el fragmento global y pasar los datos del elemento clicado
                adapter.openGlobalFragment(itemView.context, clickedItem)
            }
        }
    }


}