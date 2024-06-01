package com.example.playtronic

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*

class CampeonatosAdapter(private val campeonatosList: List<Campeonatos>) : RecyclerView.Adapter<CampeonatosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampeonatosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.campeonatos_item, parent, false)
        return CampeonatosViewHolder(view)
    }

    override fun onBindViewHolder(holder: CampeonatosViewHolder, position: Int) {
        val campeonatos = campeonatosList[position]
        // Aquí puedes usar una biblioteca como Glide o Picasso para cargar la imagen desde la URL
        Glide.with(holder.campeonatosImage.context).load(campeonatos.image).into(holder.campeonatosImage)
        holder.campeonatosTitle.text = campeonatos.title

        holder.campeonatosDate.text = campeonatos.date
    }

    override fun getItemCount() = campeonatosList.size
}