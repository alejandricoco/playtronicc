package com.example.playtronic

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class MensajesAdapter(var mensajes: MutableList<Mensaje>) : RecyclerView.Adapter<MensajesAdapter.MensajeViewHolder>() {

    private val coloresAsignados = HashMap<String, Int>()

    class MensajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNombreRemitente: TextView = itemView.findViewById(R.id.textViewNombreRemitente)
        val textViewFecha: TextView = itemView.findViewById(R.id.textViewFecha)
        val textViewContenido: TextView = itemView.findViewById(R.id.textViewContenido)
        // Puedes añadir más vistas aquí si tu diseño de mensaje lo requiere

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mensaje, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val mensaje = mensajes[position]
        holder.textViewNombreRemitente.text = mensaje.nombreRemitente
        holder.textViewFecha.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
            timeZone = android.icu.util.TimeZone.getTimeZone("Europe/Madrid") }.format(Date(mensaje.fechaEnvio))

        holder.textViewContenido.text = mensaje.contenido
        // Configura las demás vistas aquí

        val colores = listOf(
            //10 colores cómo amarillo, azul, verde, rojo, que no esten guardados en el archivo colors.xml
            R.color.color1,
            R.color.color2,
            R.color.color3,
            R.color.color4,
            R.color.color5,
            R.color.color6,
            R.color.color7,
        )


        // Asigna un color al nombre del remitente basándote en su ID
        val color: Int
        if (coloresAsignados.containsKey(mensaje.idRemitente)) {
            // Si el usuario ya tiene un color asignado, úsalo
            color = coloresAsignados[mensaje.idRemitente]!!
        } else {
            // Si el usuario no tiene un color asignado, asigna el siguiente color disponible
            val colorIndex = coloresAsignados.size % colores.size
            color = colores[colorIndex]
            coloresAsignados[mensaje.idRemitente] = color
        }

        holder.textViewNombreRemitente.setTextColor(ContextCompat.getColor(holder.itemView.context, color))
    }

    override fun getItemCount() = mensajes.size

    fun clear() {
        mensajes.clear()
    }

    fun addAll(newMensajes: List<Mensaje>) {
        mensajes.addAll(newMensajes)
    }

}