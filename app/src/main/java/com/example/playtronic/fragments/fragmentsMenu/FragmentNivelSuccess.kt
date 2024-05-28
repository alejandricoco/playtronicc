package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager.TAG
import com.example.playtronic.MenuActivity
import com.example.playtronic.R
import com.example.playtronic.fragments.fragmentsMain.FragmentLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FragmentNivelSuccess : Fragment() {

    private lateinit var btnAceptarNivelSuccess: Button
    private lateinit var tvTitulito: TextView
    private lateinit var tvCuerpoLvlObtenido: TextView
    private lateinit var logoNivelObtenido: ImageView
    private lateinit var progressBarNivel: ProgressBar
    private lateinit var tvCalculandoNivel: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nivel_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actualizarNivelUsuario()


        btnAceptarNivelSuccess = view.findViewById(R.id.buttonAceptarNivelSuccess)
        tvTitulito = view.findViewById(R.id.tvTitulito)
        tvCuerpoLvlObtenido = view.findViewById(R.id.tvCuerpoLvlObtenido)
        logoNivelObtenido = view.findViewById(R.id.logoNivelObtenido)
        progressBarNivel = view.findViewById(R.id.progressBarNivel)
        tvCalculandoNivel = view.findViewById(R.id.tvCalculandoNivel)


        // Crea un Handler para ejecutar un bloque de código después de un retraso de 3 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            // Oculta la ProgressBar y tvCalculandoNivel
            progressBarNivel.visibility = View.GONE
            tvCalculandoNivel.visibility = View.GONE

            // Muestra los otros elementos
            btnAceptarNivelSuccess.visibility = View.VISIBLE
            tvTitulito.visibility = View.VISIBLE
            tvCuerpoLvlObtenido.visibility = View.VISIBLE
            logoNivelObtenido.visibility = View.VISIBLE
        }, 3000) // 3 segundos


        btnAceptarNivelSuccess.setOnClickListener {
            volverAMenuActivity()
        }

    }

    private fun actualizarNivelUsuario() {
        val currentUser = auth.currentUser
        val userName = currentUser?.displayName ?: ""
        val userEmail = currentUser?.email ?: ""

        if (userName.isNotEmpty()) {
            buscarNivelPorUsuario(userName, userEmail)
        } else {
            buscarPorCorreoElectronico(userEmail)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun buscarNivelPorUsuario(userName: String, userEmail: String) {
        val docRef = db.collection("users").whereEqualTo("usuario", userName)
        docRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val nivel = documentSnapshot.getDouble("nivel")
                    if (nivel != null) {
                        val textViewNivel: TextView = view?.findViewById(R.id.tvTitulito) ?: return@addOnSuccessListener
                        textViewNivel.text = "¡Enhorabuena!\nHas obtenido el nivel " + nivel.toString()
                    }
                } else {
                    Log.d(TAG, "No se encontraron documentos para usuario: $userName")
                    buscarPorCorreoElectronico(userEmail)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error obteniendo documentos: ", exception)
            }
    }

    @SuppressLint("RestrictedApi")
    private fun buscarPorCorreoElectronico(userEmail: String) {
        val docRef = db.collection("users").whereEqualTo("email", userEmail)
        docRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val nivel = documentSnapshot.getDouble("nivel")
                    if (nivel != null) {
                        val textViewNivel: TextView = view?.findViewById(R.id.tvTitulito) ?: return@addOnSuccessListener
                        textViewNivel.text = "¡Enhorabuena!\nHas obtenido el nivel " + nivel.toString()
                    }
                } else {
                    Log.d(TAG, "No se encontraron documentos para email: $userEmail")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error obteniendo documentos: ", exception)
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

    private fun volverAMenuActivity() {
        val intent = Intent(requireContext(), MenuActivity::class.java)
        startActivity(intent)
    }


}