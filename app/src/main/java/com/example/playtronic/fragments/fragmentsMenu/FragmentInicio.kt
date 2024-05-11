package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.TAG
import com.example.playtronic.R
import androidx.viewpager2.widget.ViewPager2
import com.example.playtronic.ImageSliderAdapter
import com.example.playtronic.MenuActivity
import com.example.playtronic.fragments.fragmentsMain.FragmentLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FragmentInicio : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorLayout: LinearLayout
    private lateinit var sliderHandler: Handler
    private lateinit var sliderRunnable: Runnable
    private val db = FirebaseFirestore.getInstance()
    private val images = listOf(
        R.drawable.reservas,
        R.drawable.campionatos,
        R.drawable.eventos,
        R.drawable.contacto
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPagerImageSlider)
        indicatorLayout = view.findViewById(R.id.layoutIndicator)

        val fragments = listOf(
            FragmentReservas::class.java,
            FragmentCampeonatos::class.java,
            FragmentEventos::class.java,
            FragmentContacto::class.java
        )

        val fragmentManager = requireActivity().supportFragmentManager
        val binding = (requireActivity() as MenuActivity).binding
        val adapter = ImageSliderAdapter(images, fragments, fragmentManager, binding)
        viewPager.adapter = adapter
        setupIndicators(images.size)

        sliderHandler = Handler(Looper.getMainLooper())
        sliderRunnable = Runnable {
            if (isAdded) { // Verifica que el fragmento esté agregado a la actividad
                viewPager.currentItem = (viewPager.currentItem + 1) % images.size
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        startImageSlider(images.size)

        comprobarReservasUsuario()
    }

    // ... (Mantén las funciones setupIndicators, setCurrentIndicator y startImageSlider aquí)

    private fun setupIndicators(count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(context)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive // Asegúrate de tener este drawable
                    )
                )
                it.layoutParams = layoutParams
                indicatorLayout.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicatorLayout.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorLayout.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_active // Asegúrate de tener este drawable
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive // Asegúrate de tener este drawable
                    )
                )
            }
        }
    }

    private fun startImageSlider(count: Int) {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                sliderHandler.removeCallbacks(sliderRunnable)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    sliderHandler.postDelayed(sliderRunnable, 5000) // Cambia a 5 segundos
                }
            }
        })
        sliderHandler.postDelayed(sliderRunnable, 5000) // Inicia el primer deslizamiento después de 5 segundos
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 5000)
    }



    private fun cargarFragment(fragment: Fragment) {
        // Asegúrate de que la actividad contenedora no sea nula
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.drawer_layout, fragment) // cargamos en el drawer del MenuActivity el fragmento que queremos
        fragmentTransaction.addToBackStack(null) // Permite volver al FragmentLogin al presionar el botón atrás
        fragmentTransaction.commit()
    }


    @SuppressLint("RestrictedApi")
    private fun comprobarReservasUsuario() {
        val textViewReservas = view?.findViewById<TextView>(R.id.tvReservas) // Asegúrate de reemplazar 'textViewReservas' con el ID real de tu TextView
        val usuario = FirebaseAuth.getInstance().currentUser?.displayName
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        if (usuario != null) {
            db.collection("reservas")
                .whereEqualTo("usuario", usuario)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        textViewReservas?.text = "No has reservado ninguna pista... A qué esperas?"
                    } else {
                        val reservas = StringBuilder("Reservas:\n")
                        for (document in result) {
                            val deporte = document.getString("deporte")
                            val pista = document.getString("pista")
                            val dia = document.getString("dia")
                            val hora = document.getString("hora")
                            reservas.append("$deporte, $pista, $dia, $hora\n")
                        }
                        textViewReservas?.text = reservas.toString()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error obteniendo las reservas.", exception)
                }
        } else if (userEmail != null) {
            // Si no hay reservas para el usuario, intenta obtener el nombre del usuario de la colección "users"
            db.collection("users").document(userEmail).get()
                .addOnSuccessListener { document ->
                    val nombre = document.getString("nombre")
                    if (nombre != null) {
                        // Busca las reservas para el nombre del usuario
                        db.collection("reservas")
                            .whereEqualTo("usuario", nombre)
                            .get()
                            .addOnSuccessListener { resultNombre ->
                                if (resultNombre.isEmpty) {
                                    textViewReservas?.text = "No has reservado ninguna pista... A qué esperas?"
                                } else {
                                    val reservas = StringBuilder("Reservas:\n")
                                    for (document in resultNombre) {
                                        val deporte = document.getString("deporte")
                                        val pista = document.getString("pista")
                                        val dia = document.getString("dia")
                                        val hora = document.getString("hora")
                                        reservas.append("$deporte, $pista, $dia, $hora\n")
                                    }
                                    textViewReservas?.text = reservas.toString()
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w(TAG, "Error obteniendo las reservas.", exception)
                            }
                    } else {
                        Toast.makeText(context, "No se pudo obtener el nombre de usuario.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error obteniendo el nombre de usuario.", exception)
                }
        } else {
            Toast.makeText(context, "Por favor, inicia sesión para ver tus reservas.", Toast.LENGTH_SHORT).show()
        }
    }

}