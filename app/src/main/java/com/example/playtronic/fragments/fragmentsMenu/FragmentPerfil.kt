package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager.TAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playtronic.Partido
import com.example.playtronic.PartidoEliminarAdapter
import com.example.playtronic.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class FragmentPerfil : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var nombreUsuario: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }


    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        nombreUsuario = sharedPreferences.getString("nombreUsuario", "Default")

        val profileImage = view.findViewById<ImageView>(R.id.profile_image)
        val profileName = view.findViewById<TextView>(R.id.profile_name)

        val user = auth.currentUser
        if (user != null) {
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .into(profileImage)
            } else {
                Glide.with(this)
                    .load("https://www.iprcenter.gov/image-repository/blank-profile-picture.png")
                    .into(profileImage)
            }
            val username = user.displayName
            if (username != null) {
                profileName.setText(username)
            } else {

            }
        }

        val profileLevel = view.findViewById<TextView>(R.id.profile_level)
        val db = FirebaseFirestore.getInstance()

        val userEmail = user?.email
        val twUser = user?.displayName

        if (userEmail != null) {
            // Fetch document using userEmail
            val emailDocRef = db.collection("users").document(userEmail)
            emailDocRef.get().addOnSuccessListener { emailDoc ->
                if (emailDoc.exists()) {
                    profileName.setText(emailDoc.getString("nombre"))
                    handleDocument(emailDoc, db, profileLevel)
                } else {
                    // Fetch document using Google email
                    val googleDocRef = db.collection("users").document("(Google) $userEmail")
                    googleDocRef.get().addOnSuccessListener { googleDoc ->
                        if (googleDoc.exists()) {
                            handleDocument(googleDoc, db, profileLevel)
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
                    handleDocument(twitterDoc, db, profileLevel)
                } else {
                    Log.w(TAG, "No document found for user")
                }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error getting Twitter document", e)
            }
        } else {
            Log.w(TAG, "User is not authenticated")
        }

        val recyclerViewMisPartidos = view.findViewById<RecyclerView>(R.id.recyclerViewMisPartidos)
        recyclerViewMisPartidos.layoutManager = LinearLayoutManager(context)

        // Obtén el ID del usuario
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid

        if (usuarioId != null) {
            // Accede a la colección de partidos
            val db = FirebaseFirestore.getInstance()
            db.collection("partidos").get().addOnSuccessListener { result ->
                val partidos = result.map { document ->
                    Partido(
                        id = document.id,
                        creadoPor = document.getString("creadoPor") ?: "",
                        deporte = document.getString("deporte") ?: "",
                        horarioPreferido = document.getString("horarioPreferido") ?: "",
                        nivelOponente = document.getString("nivelOponente") ?: "",
                        usuariosUnidos = (document.get("usuariosUnidos") as? List<String>) ?: listOf()
                    )
                }.filter { partido ->
                    // Filtra los partidos creados por el usuario actual
                    val user = FirebaseAuth.getInstance().currentUser
                    val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
                    //Si el usuario firebase es nulo, se obtiene el nombre de usuario guardado en SharedPreferences
                    val username = user?.displayName ?: sharedPreferences.getString("nombreUsuario", "Default")
                    partido.creadoPor == username || partido.usuariosUnidos.contains(username)
                }

                // Configura el RecyclerView
                recyclerViewMisPartidos.adapter = PartidoEliminarAdapter(partidos.toMutableList())
            }
        }
    }

    private fun handleDocument(document: DocumentSnapshot, db: FirebaseFirestore, profileLevel: TextView) {
        val nivel = document.getDouble("nivel")
        profileLevel.text = if (nivel != null) {
            when {
                nivel <= 3.4 -> "$nivel (Casi malo)"
                nivel <= 7.0 -> "$nivel (Casi bueno)"
                else -> "$nivel (Leyenda)"
            }
        } else {
            "Sin nivel"
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