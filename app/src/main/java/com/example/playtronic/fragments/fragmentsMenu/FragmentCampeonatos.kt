package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playtronic.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*


class FragmentCampeonatos : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_campeonatos, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // A PARTIR DE AQUI SON LOS EVENTOS
        val campeonatosRecyclerView: RecyclerView = view.findViewById(R.id.campeonatosRecyclerView)
        campeonatosRecyclerView.layoutManager = LinearLayoutManager(context)

        db.collection("campeonatos")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val campeonatosList = result.map { document ->
                    val date = document.getTimestamp("date")!!.toDate()
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                    val cuerpo = document.getString("cuerpo")?: ""
                    Campeonatos(document.getString("image")!!, document.getString("title")!!,
                        formattedDate, cuerpo)
                }
                campeonatosRecyclerView.adapter = CampeonatosAdapter(campeonatosList)
            }
            .addOnFailureListener { exception ->
                Log.w(FragmentManager.TAG, "Error obteniendo los campeonatos.", exception)
            }

    }


    private fun cargarFragment(fragment: Fragment) {
        // Asegúrate de que la actividad contenedora no sea nula
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.drawer_layout, fragment) // cargamos en el drawer del MenuActivity el fragmento que queremos
        fragmentTransaction.addToBackStack(null) // Permite volver al FragmentLogin al presionar el botón atrás
        fragmentTransaction.commit()
    }
}