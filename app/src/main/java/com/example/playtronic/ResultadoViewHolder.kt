package com.example.playtronic

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val deporte: TextView = itemView.findViewById(R.id.tvDeporteResultado)
    val fecha: TextView = itemView.findViewById(R.id.tvFechaResultado)
    val winlose: TextView = itemView.findViewById(R.id.tvWinLoseResultado)
    val set1: TextView = itemView.findViewById(R.id.tvSet1Resultado)
    val set2: TextView = itemView.findViewById(R.id.tvSet2Resultado)
    val set3: TextView = itemView.findViewById(R.id.tvSet3Resultado)

    fun bind(resultado: Resultado) {
        deporte.text = "Partido de " + resultado.deporte
        fecha.text = resultado.fecha
        winlose.text = resultado.winlose
        set1.text = "${resultado.set1_1}-${resultado.set1_2} /"
        set2.text = " ${resultado.set2_1}-${resultado.set2_2}"
        set3.text = if (resultado.set3_1 == "-" && resultado.set3_2 == "-") "" else " / ${resultado.set3_1}-${resultado.set3_2}"
    }
}