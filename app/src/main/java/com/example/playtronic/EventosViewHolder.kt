package com.example.playtronic

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//SE ENCARGA DE MOSTRAR LA NOTICIA EN EL RECYCLERVIEW
class EventosViewHolder(view: View, private val adapter: EventosAdapter) : RecyclerView.ViewHolder(view) {
    val eventosImage: ImageView = view.findViewById(R.id.eventosImage)
    val eventosTitle: TextView = view.findViewById(R.id.eventosTitle)
    val eventosDate: TextView = view.findViewById(R.id.eventosDate)


    init {
        itemView.setOnClickListener {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) { // Comprueba si el elemento aún existe
                val clickedItem = adapter.eventosList[position]
                // Aquí puedes llamar a tu método para abrir el fragmento global y pasar los datos del elemento clicado
                adapter.openGlobalFragment(itemView.context, clickedItem)
            }
        }
    }


}