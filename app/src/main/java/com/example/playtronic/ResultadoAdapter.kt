package com.example.playtronic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ResultadoAdapter(private val resultados: List<Resultado>) : RecyclerView.Adapter<ResultadoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultadoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_resultado, parent, false)
        return ResultadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultadoViewHolder, position: Int) {
        holder.bind(resultados[position])
    }

    override fun getItemCount() = resultados.size
}