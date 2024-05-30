package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager.TAG
import com.example.playtronic.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


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