package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.ParseException
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager.TAG
import com.bumptech.glide.Glide
import com.example.playtronic.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class FragmentNivel : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nivel, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nivelView = view.findViewById<NivelView>(R.id.nivelView)
        val profileImage = view.findViewById<ImageView>(R.id.profile_image)
        val profileName = view.findViewById<TextView>(R.id.profile_name)
        val profileLevel = view.findViewById<TextView>(R.id.profile_level)
        val tvPJValor = view.findViewById<TextView>(R.id.tvPJValor)
        val tvPGValor = view.findViewById<TextView>(R.id.tvPGValor)
        val tvPPValor = view.findViewById<TextView>(R.id.tvPPValor)
        val tvPorcentajeVictoriasValor = view.findViewById<TextView>(R.id.tvPorcentajeVictoriasValor)
        val tvMayorRachaValor = view.findViewById<TextView>(R.id.tvMayorRachaValor)
        val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        val userEmail = user?.email
        val twUser = user?.displayName
        var docId = ""

        if (userEmail != null) {
            docId = if (userEmail.contains("@")) "(Google) $userEmail" else userEmail
        } else if (twUser != null) {
            docId = "(Twitter) $twUser"
        }

        db.collection("users").document(docId)
            .get()
            .addOnSuccessListener { document ->
                val listaNiveles = document.get("listaNiveles") as? List<Float> ?: listOf()
                nivelView.setNiveles(listaNiveles)

                val photoUrl = document.getString("photourl")
                if (photoUrl != null) {
                    Glide.with(this)
                        .load(photoUrl)
                        .into(profileImage)
                } else {
                    Glide.with(this)
                        .load("https://www.iprcenter.gov/image-repository/blank-profile-picture.png")
                        .into(profileImage)
                }

                val username = document.getString("usuario")
                profileName.text = username

                val nivel = document.getDouble("nivel")
                profileLevel.text = if (nivel != null) {
                    when {
                        nivel <= 3.4 -> "$nivel (Casi malo)"
                        nivel <= 7.0 -> "$nivel (Casi bueno)"
                        else -> "$nivel (Leyenda)"
                    }
                } else {
                    "Nivel no disponible"
                }

                db.collection("resultados")
                    .whereEqualTo("usuario", username)
                    .get()
                    .addOnSuccessListener { result ->
                        val totalPartidos = result.size()
                        tvPJValor.text = totalPartidos.toString()

                        val victorias = result.count { it.getString("winlose") == "Victoria" }
                        tvPGValor.text = victorias.toString()

                        val derrotas = result.count { it.getString("winlose") == "Derrota" }
                        tvPPValor.text = derrotas.toString()

                        val porcentajeVictorias = if (totalPartidos > 0) victorias * 100 / totalPartidos else 0
                        tvPorcentajeVictoriasValor.text = "$porcentajeVictorias%"

                        val sortedResults = result.sortedBy {
                            val dateString = it.getString("fecha")
                            var date: Date? = null
                            try {
                                date = dateFormat.parse(dateString)
                            } catch (e: ParseException) {
                                Log.e(TAG, "Error al parsear la fecha", e)
                            }
                            date }

                        var maxRacha = 0
                        var rachaActual = 0
                        for (resultado in sortedResults) {
                            if (resultado.getString("winlose") == "Victoria") {
                                rachaActual++
                                if (rachaActual > maxRacha) {
                                    maxRacha = rachaActual
                                }
                            } else {
                                rachaActual = 0
                            }
                        }
                        tvMayorRachaValor.text = maxRacha.toString()
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al obtener el documento del usuario", e)
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