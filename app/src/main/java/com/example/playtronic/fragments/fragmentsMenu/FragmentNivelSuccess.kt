package com.example.playtronic.fragments.fragmentsMenu

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
        val tvTitulito = view?.findViewById<TextView>(R.id.tvTitulito)
        val user = FirebaseAuth.getInstance().currentUser
        val userName = user?.displayName

        if (userName != null) {
            val docRef = db.collection("users").whereEqualTo("usuario", userName)
            docRef.get().addOnSuccessListener { result ->
                if (isAdded) { // Verifica si el fragmento todavía está adjunto a la actividad
                    if (!result.isEmpty) {
                        val document = result.documents[0]
                        val nivel = document.getDouble("nivel")
                        if (nivel != null) {
                            // Si el nivel no es null, actualiza el texto del TextView
                            val nivelTexto = "¡Enhorabuena!\nHas obtenido el nivel $nivel"
                            tvTitulito?.text = nivelTexto
                        } else {
                            // No hacemos nada si el nivel es null
                        }
                    }
                }
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

    private fun volverAMenuActivity() {
        val intent = Intent(requireContext(), MenuActivity::class.java)
        startActivity(intent)
    }


}