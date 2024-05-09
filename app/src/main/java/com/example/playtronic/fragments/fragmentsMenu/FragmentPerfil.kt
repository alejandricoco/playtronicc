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
import androidx.fragment.app.FragmentManager.TAG
import com.bumptech.glide.Glide
import com.example.playtronic.R
import com.google.firebase.auth.FirebaseAuth
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        nombreUsuario = sharedPreferences.getString("nombreUsuario", "Default")

        val profileImage = view.findViewById<ImageView>(R.id.profile_image)
        val profileName = view.findViewById<TextView>(R.id.profile_name)

        val user = auth.currentUser
        if (user != null) {
            val photoUrl = user.photoUrl
            if (photoUrl != null)
                {
                    Glide.with(this)
                        .load(photoUrl)
                        .into(profileImage)
                }
            else{
                    Glide.with(this)
                        .load("https://www.iprcenter.gov/image-repository/blank-profile-picture.png")
                        .into(profileImage)
                }
            val username = user.displayName
            if (username != null)
                {
                    profileName.setText(username)
                }
            else
                {
                    // Si el usuario no tiene nombre de usuario, se muestra el nombre de usuario guardado en SharedPreferences
                    profileName.setText(nombreUsuario)
                }
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