package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.TAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.playtronic.*
import com.example.playtronic.fragments.fragmentsMain.FragmentLogin
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
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

    @SuppressLint("RestrictedApi")
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

        actualizarNivelUsuario()

        // A PARTIR DE AQUI SON LAS NOTICIAS
        val newsRecyclerView: RecyclerView = view.findViewById(R.id.newsRecyclerView)
        newsRecyclerView.layoutManager = LinearLayoutManager(context)

        db.collection("news")
            .get()
            .addOnSuccessListener { result ->
                val newsList = result.map { document ->
                    News(document.getString("image")!!, document.getString("title")!!)
                }
                newsRecyclerView.adapter = NewsAdapter(newsList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error obteniendo las noticias.", exception)
            }
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
        val progressBarReservas = view?.findViewById<ProgressBar>(R.id.progressBarReservas)
        val textViewReservas = view?.findViewById<TextView>(R.id.tvReservas)
        val usuario = FirebaseAuth.getInstance().currentUser?.displayName
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        if (usuario != null) {
            db.collection("reservas")
                .whereEqualTo("usuario", usuario)
                .get()
                .addOnSuccessListener { result ->
                    if (isAdded) { // Verifica si el fragmento todavía está adjunto a la actividad
                        if (result.isEmpty) {
                            progressBarReservas?.visibility = View.GONE
                            textViewReservas?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            textViewReservas?.text = "No has reservado ninguna pista...\nA qué esperas?"
                        } else {
                            val reservas = result.map { document ->
                                val deporte = document.getString("deporte")
                                val pista = document.getString("pista")
                                val dia = document.getString("dia")
                                val hora = document.getString("hora")
                                "• $deporte, $pista, $dia, $hora h"
                            }.joinToString("\n\n")

                            val spannable = SpannableString(reservas)
                            // Colorea cada punto amarillo
                            var start = 0 // El índice del primer carácter del primer punto
                            reservas.split("\n\n").forEach { reserva ->
                                val end = start + 1
                                spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.yellow)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                start = end + reserva.length + 1 // Ajusta el índice de inicio para el próximo punto
                            }

                            textViewReservas?.text = spannable
                            //setea el color del texto a white
                            textViewReservas?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            progressBarReservas?.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error obteniendo las reservas.", exception)
                }
        } else if (userEmail != null) {
            db.collection("users").document(userEmail).get()
                .addOnSuccessListener { document ->
                    val nombre = document.getString("nombre")
                    if (nombre != null) {
                        db.collection("reservas")
                            .whereEqualTo("usuario", nombre)
                            .get()
                            .addOnSuccessListener { resultNombre ->
                                if (isAdded) { // Verifica si el fragmento todavía está adjunto a la actividad
                                    if (resultNombre.isEmpty) {
                                        progressBarReservas?.visibility = View.GONE
                                        textViewReservas?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                        textViewReservas?.text = "No has reservado ninguna pista...\nA qué esperas?"
                                    } else {
                                        val reservas = resultNombre.map { document ->
                                            val deporte = document.getString("deporte")
                                            val pista = document.getString("pista")
                                            val dia = document.getString("dia")
                                            val hora = document.getString("hora")
                                            "• $deporte, $pista, $dia, $hora h"
                                        }.joinToString("\n\n")

                                        val spannable = SpannableString(reservas)
                                        // Colorea cada punto amarillo
                                        var start = 0 // El índice del primer carácter del primer punto
                                        reservas.split("\n\n").forEach { reserva ->
                                            val end = start + 1
                                            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.yellow)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                            start = end + reserva.length + 1 // Ajusta el índice de inicio para el próximo punto
                                        }

                                        textViewReservas?.text = spannable
                                        textViewReservas?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                        progressBarReservas?.visibility = View.GONE
                                    }
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

    private fun actualizarNivelUsuario() {
        val tvNivel = view?.findViewById<TextView>(R.id.tvNivel)
        val progressBarCont = view?.findViewById<LinearLayout>(R.id.progressBarContainer)
        val linearLayoutImgYTexto = view?.findViewById<LinearLayout>(R.id.linearLayoutImgYTexto)
        val progressBarNivel = view?.findViewById<ProgressBar>(R.id.progressBarNivel)
        val iconoNivel = view?.findViewById<ImageView>(R.id.iconoNivel)
        val user = FirebaseAuth.getInstance().currentUser
        val userName = user?.displayName
        val userEmail = user?.email

        if (userName != null) {
            val docRef = db.collection("users").whereEqualTo("usuario", userName)
            docRef.get().addOnSuccessListener { result ->
                if (isAdded) { // Verifica si el fragmento todavía está adjunto a la actividad
                    if (!result.isEmpty) {
                        val document = result.documents[0]
                        val nivel = document.getDouble("nivel")
                        if (nivel != null) {
                            // Si el nivel no es null, actualiza el texto del TextView
                            val nivelTexto = "$nivel/10"
                            val spannable = SpannableString(nivelTexto)
                            spannable.setSpan(RelativeSizeSpan(0.5f), nivelTexto.indexOf("/"), nivelTexto.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            // Cambia el color del "nivel" a "greyMasClaro"
                            val colorGreyMasClaro = ContextCompat.getColor(requireContext(), R.color.greyMasClaro)
                            spannable.setSpan(ForegroundColorSpan(colorGreyMasClaro), nivelTexto.indexOf("/"), nivelTexto.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                            tvNivel?.text = spannable
                            tvNivel?.textSize = 35f // Aumenta el tamaño de la fuente
                            tvNivel?.gravity = Gravity.CENTER_HORIZONTAL // Centra el texto
                            progressBarNivel?.visibility = View.GONE
                            progressBarCont?.visibility = View.VISIBLE
                            linearLayoutImgYTexto?.visibility = View.VISIBLE

                            // Actualiza las ProgressBar
                            val progressBars = listOf(
                                view?.findViewById<ProgressBar>(R.id.progressBar1),
                                view?.findViewById<ProgressBar>(R.id.progressBar2),
                                view?.findViewById<ProgressBar>(R.id.progressBar3),
                                view?.findViewById<ProgressBar>(R.id.progressBar4),
                                view?.findViewById<ProgressBar>(R.id.progressBar5)
                            )
                            progressBars.forEachIndexed { index, progressBar ->
                                val progress = ((nivel - index * 2) * 50).coerceAtLeast(0.0).coerceAtMost(100.0)
                                progressBar?.progress = progress.toInt()
                            }

                        } else {
                            // Si el nivel es null, deja el mensaje que ya está
                            //tvNivel?.text = "Actualmente no dispones\nde nivel Playtronic.\nCompleta el cuestionario\nen la sección jugar\npara obtenerlo."
                            tvNivel?.textSize = 13f // Tamaño de la fuente original
                            progressBarNivel?.visibility = View.GONE
                            linearLayoutImgYTexto?.visibility = View.VISIBLE

                            iconoNivel?.visibility = View.GONE
                            progressBarCont?.visibility = View.GONE
                        }
                    }
                }
            }
        }
        else if (userEmail != null) {
            val docRef = db.collection("users").document(userEmail)
            docRef.get().addOnSuccessListener { document ->
                if (isAdded) { // Verifica si el fragmento todavía está adjunto a la actividad
                    val nivel = document.getDouble("nivel")
                    if (nivel != null) {
                        // Si el nivel no es null, actualiza el texto del TextView
                        val nivelTexto = "$nivel/10"
                        val spannable = SpannableString(nivelTexto)
                        spannable.setSpan(RelativeSizeSpan(0.5f), nivelTexto.indexOf("/"), nivelTexto.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        // Cambia el color del "nivel" a "greyMasClaro"
                        val colorGreyMasClaro = ContextCompat.getColor(requireContext(), R.color.greyMasClaro)
                        spannable.setSpan(ForegroundColorSpan(colorGreyMasClaro), nivelTexto.indexOf("/"), nivelTexto.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        tvNivel?.text = spannable
                        tvNivel?.textSize = 35f // Aumenta el tamaño de la fuente
                        tvNivel?.gravity = Gravity.CENTER_HORIZONTAL // Centra el texto
                        progressBarNivel?.visibility = View.GONE
                        progressBarCont?.visibility = View.VISIBLE
                        linearLayoutImgYTexto?.visibility = View.VISIBLE

                        // Actualiza las ProgressBar
                        val progressBars = listOf(
                            view?.findViewById<ProgressBar>(R.id.progressBar1),
                            view?.findViewById<ProgressBar>(R.id.progressBar2),
                            view?.findViewById<ProgressBar>(R.id.progressBar3),
                            view?.findViewById<ProgressBar>(R.id.progressBar4),
                            view?.findViewById<ProgressBar>(R.id.progressBar5)
                        )
                        progressBars.forEachIndexed { index, progressBar ->
                            val progress = ((nivel - index * 2) * 50).coerceAtLeast(0.0).coerceAtMost(100.0)
                            progressBar?.progress = progress.toInt()
                        }

                    } else {
                        // Si el nivel es null, deja el mensaje que ya está
                        //tvNivel?.text = "Actualmente no dispones\nde nivel Playtronic.\nCompleta el cuestionario\nen la sección jugar\npara obtenerlo."
                        tvNivel?.textSize = 13f // Tamaño de la fuente original
                        progressBarNivel?.visibility = View.GONE
                        linearLayoutImgYTexto?.visibility = View.VISIBLE

                        iconoNivel?.visibility = View.GONE
                        progressBarCont?.visibility = View.GONE
                    }
                }
            }
        }

    }

}