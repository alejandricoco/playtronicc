package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.content.Context
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        val userEmail = user?.email
        val twUser = user?.displayName

        if (userEmail != null) {
            // Fetch document using userEmail
            val emailDocRef = db.collection("users").document(userEmail)
            emailDocRef.get().addOnSuccessListener { emailDoc ->
                if (emailDoc.exists()) {
                    handleDocument(emailDoc, db)
                } else {
                    // Fetch document using Google email
                    val googleDocRef = db.collection("users").document("(Google) $userEmail")
                    googleDocRef.get().addOnSuccessListener { googleDoc ->
                        if (googleDoc.exists()) {
                            handleDocument(googleDoc, db)
                        } else {
                            Log.w(TAG, "No document found for user")
                        }
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error getting Google document", e)
                    }
                }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error getting email document", e)
            }
        } else if (twUser != null) {
            // Fetch document using Twitter username
            val twitterDocRef = db.collection("users").document("(Twitter) $twUser")
            twitterDocRef.get().addOnSuccessListener { twitterDoc ->
                if (twitterDoc.exists()) {
                    handleDocument(twitterDoc, db)
                } else {
                    Log.w(TAG, "No document found for user")
                }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error getting Twitter document", e)
            }
        } else {
            Log.w(TAG, "User is not authenticated")
        }
    }

    @SuppressLint("RestrictedApi")
    private fun handleDocument(document: DocumentSnapshot, db: FirebaseFirestore) {
        val listaNiveles = document.get("listaNiveles") as? List<Float> ?: listOf()

        val nivelView = view?.findViewById<NivelView>(R.id.nivelView)
        val profileImage = view?.findViewById<ImageView>(R.id.profile_image)
        val profileName = view?.findViewById<TextView>(R.id.profile_name)
        val profileLevel = view?.findViewById<TextView>(R.id.profile_level)
        val tvPJValor = view?.findViewById<TextView>(R.id.tvPJValor)
        val tvPGValor = view?.findViewById<TextView>(R.id.tvPGValor)
        val tvPPValor = view?.findViewById<TextView>(R.id.tvPPValor)
        val tvPorcentajeVictoriasValor = view?.findViewById<TextView>(R.id.tvPorcentajeVictoriasValor)
        val tvMayorRachaValor = view?.findViewById<TextView>(R.id.tvMayorRachaValor)
        val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())

        nivelView?.setNiveles(listaNiveles)
        val photoUrl = document.getString("photourl")
        if (photoUrl != null) {
            Glide.with(this)
                .load(photoUrl)
                .into(profileImage!!)
        } else {
            Glide.with(this)
                .load("https://www.iprcenter.gov/image-repository/blank-profile-picture.png")
                .into(profileImage!!)
        }

        val nombre = document.getString("nombre")
        val username = document.getString("usuario")
        val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val nombreUsuario = sharedPreferences.getString("nombreUsuario", "Default")

        if (nombre != null) {
            profileName?.text = nombre
        } else {
            profileName?.text = username ?: nombreUsuario
        }


        val nivel = document.getDouble("nivel")
        profileLevel?.text = if (nivel != null) {
            when {
                nivel <= 3.4 -> "$nivel (Casi malo)"
                nivel <= 7.0 -> "$nivel (Casi bueno)"
                else -> "$nivel (Leyenda)"
            }
        } else {
            "Nivel no disponible"
        }

        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        GlobalScope.launch(Dispatchers.Main) {


            val nombre = getUserNameByEmail(db, email ?: "")
            // Aquí puedes usar 'nombre'

            db.collection("resultados")
                .whereEqualTo("usuario", nombre ?: username)
                .get()
                .addOnSuccessListener { result ->


                    val totalPartidos = result.size()
                    tvPJValor?.text = totalPartidos.toString()

                    val victorias = result.count { it.getString("winlose") == "Victoria" }
                    tvPGValor?.text = victorias.toString()

                    val derrotas = result.count { it.getString("winlose") == "Derrota" }
                    tvPPValor?.text = derrotas.toString()

                    val porcentajeVictorias = if (totalPartidos > 0) victorias * 100 / totalPartidos else 0
                    tvPorcentajeVictoriasValor?.text = "$porcentajeVictorias%"

                    val sortedResults = result.sortedBy {
                        val timestamp = it.getTimestamp("fecha")
                        var date: Date? = null
                        if (timestamp != null) {
                            date = timestamp.toDate()
                        }
                        date
                    }

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
                    tvMayorRachaValor?.text = maxRacha.toString()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error al obtener los resultados del usuario", e)
                }

        }
    }

    suspend fun getUserNameByEmail(db: FirebaseFirestore, email: String): String? = withContext(Dispatchers.IO) {
        if (email.isNotBlank()) {
            val docRef = db.collection("users").document(email)
            val document = docRef.get().await()
            if (document != null && document.exists()) {
                document.getString("nombre")
            } else {
                null
            }
        } else {
            null
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