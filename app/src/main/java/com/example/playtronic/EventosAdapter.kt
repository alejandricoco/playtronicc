package com.example.playtronic

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*

class EventosAdapter(private val eventosList: List<Eventos>) : RecyclerView.Adapter<EventosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.eventos_item, parent, false)
        return EventosViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventosViewHolder, position: Int) {
        val eventos = eventosList[position]
        // Aquí puedes usar una biblioteca como Glide o Picasso para cargar la imagen desde la URL
        Glide.with(holder.eventosImage.context).load(eventos.image).into(holder.eventosImage)
        holder.eventosTitle.text = eventos.title

        holder.eventosDate.text = eventos.date
    }

    override fun getItemCount() = eventosList.size
}