package com.example.playtronic

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playtronic.fragments.fragmentsMenu.GlobalFragment
import java.util.*

class CampeonatosAdapter(val campeonatosList: List<Campeonatos>) : RecyclerView.Adapter<CampeonatosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampeonatosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.campeonatos_item, parent, false)
        return CampeonatosViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: CampeonatosViewHolder, position: Int) {
        val campeonatos = campeonatosList[position]
        // Aquí puedes usar una biblioteca como Glide o Picasso para cargar la imagen desde la URL
        Glide.with(holder.campeonatosImage.context).load(campeonatos.image).into(holder.campeonatosImage)
        holder.campeonatosTitle.text = campeonatos.title

        holder.campeonatosDate.text = campeonatos.date
    }

    override fun getItemCount() = campeonatosList.size


    fun openGlobalFragment(context: Context, campeonatos: Campeonatos) {
        val fragment = GlobalFragment() // Reemplaza esto con tu fragmento global
        val bundle = Bundle()
        bundle.putString("image", campeonatos.image)
        bundle.putString("title", campeonatos.title)
        bundle.putString("date", campeonatos.date)
        bundle.putString("cuerpo", campeonatos.cuerpo)
        fragment.arguments = bundle
        // Realiza la transacción de fragmentos para abrir tu fragmento global
        (context as? AppCompatActivity)?.let {
            val fragmentManager = it.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
}