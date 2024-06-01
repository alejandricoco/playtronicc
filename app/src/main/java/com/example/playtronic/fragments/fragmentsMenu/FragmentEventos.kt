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
import com.example.playtronic.Eventos
import com.example.playtronic.EventosAdapter
import com.example.playtronic.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*


class FragmentEventos : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eventos, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // A PARTIR DE AQUI SON LOS EVENTOS
        val eventosRecyclerView: RecyclerView = view.findViewById(R.id.eventosRecyclerView)
        eventosRecyclerView.layoutManager = LinearLayoutManager(context)

        db.collection("eventos")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val eventosList = result.map { document ->
                    val date = document.getTimestamp("date")!!.toDate()
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                    Eventos(document.getString("image")!!, document.getString("title")!!, formattedDate)
                }
                eventosRecyclerView.adapter = EventosAdapter(eventosList)
            }
            .addOnFailureListener { exception ->
                Log.w(FragmentManager.TAG, "Error obteniendo los eventos.", exception)
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