package com.example.playtronic.fragments.fragmentsMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.playtronic.R
import com.google.firebase.auth.FirebaseAuth

class FragmentAvisoMustVerify : Fragment() {

    private lateinit var btnAceptarEnlaceAcc: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_aviso_must_verify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAceptarEnlaceAcc = view.findViewById(R.id.buttonAceptarEnlaceAcc)


        btnAceptarEnlaceAcc.setOnClickListener {
            cargarFragment(FragmentLogin())
        }


    }


    private fun cargarFragment(fragment: Fragment) {
        // Asegúrate de que la actividad contenedora no sea nula
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activityMainFrameLayout, fragment) // cargamos en el FrameLayout del activity_login el fragmento que queremos
        fragmentTransaction.addToBackStack(null) // Permite volver al FragmentLogin al presionar el botón atrás
        fragmentTransaction.commit()
    }


}