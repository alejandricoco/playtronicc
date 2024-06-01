package com.example.playtronic

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playtronic.fragments.fragmentsMenu.GlobalFragment

class EventosAdapter(val eventosList: List<Eventos>) : RecyclerView.Adapter<EventosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.eventos_item, parent, false)
        return EventosViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: EventosViewHolder, position: Int) {
        val eventos = eventosList[position]
        // Aquí puedes usar una biblioteca como Glide o Picasso para cargar la imagen desde la URL
        Glide.with(holder.eventosImage.context).load(eventos.image).into(holder.eventosImage)
        holder.eventosTitle.text = eventos.title

        holder.eventosDate.text = eventos.date
    }

    override fun getItemCount() = eventosList.size

    fun openGlobalFragment(context: Context, eventos: Eventos) {
        val fragment = GlobalFragment() // Reemplaza esto con tu fragmento global
        val bundle = Bundle()
        bundle.putString("image", eventos.image)
        bundle.putString("title", eventos.title)
        bundle.putString("date", eventos.date)
        bundle.putString("cuerpo", eventos.cuerpo)
        fragment.arguments = bundle
        // Aquí puedes realizar la transacción de fragmentos para abrir tu fragmento global
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit() // Reemplaza 'container' con el ID de tu contenedor de fragmentos
    }
}