package com.example.playtronic.fragments.fragmentsMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.viewpager2.widget.ViewPager2
import com.example.playtronic.InformationBanner
import com.example.playtronic.Question
import com.example.playtronic.QuestionAdapter
import com.example.playtronic.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FragmentJugar : Fragment() {


    private lateinit var viewPager: ViewPager2
    private lateinit var submitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_jugar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        submitButton = view.findViewById(R.id.submit_button)


        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        val twUser = user?.displayName
        val db = FirebaseFirestore.getInstance()

        if (userEmail != null) {
            val googleDocRef = db.collection("users").document("(Google) $userEmail")
            googleDocRef.get().addOnSuccessListener { googleDoc ->
                if (googleDoc.exists() && googleDoc.getDouble("nivel") != null) {
                    // El usuario ya ha calculado su nivel, no es necesario mostrar el formulario
                    view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                    submitButton.visibility = View.GONE
                    tabLayout.visibility = View.GONE
                } else {
                    val emailDocRef = db.collection("users").document(userEmail)
                    emailDocRef.get().addOnSuccessListener { emailDoc ->
                        if (emailDoc.exists() && emailDoc.getDouble("nivel") != null) {
                            // El usuario ya ha calculado su nivel, no es necesario mostrar el formulario
                            view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                            submitButton.visibility = View.GONE
                            tabLayout.visibility = View.GONE
                        }
                    }
                }
            }
        } else if (twUser != null) {
            val twitterDocRef = db.collection("users").document("(Twitter) $twUser")
            twitterDocRef.get().addOnSuccessListener { twitterDoc ->
                if (twitterDoc.exists()) {
                    val nivel = twitterDoc.getDouble("nivel")
                    if (nivel != null) {
                        // El usuario ya ha calculado su nivel, no es necesario mostrar el formulario
                        view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                        submitButton.visibility = View.GONE
                        tabLayout.visibility = View.GONE
                    } else {
                        // El nivel es null, mostrar el formulario
                        view.findViewById<CardView>(R.id.cardView).visibility = View.VISIBLE
                        submitButton.visibility = View.VISIBLE
                        tabLayout.visibility = View.VISIBLE
                    }
                } else {
                    // El documento no existe, mostrar el formulario
                    view.findViewById<CardView>(R.id.cardView).visibility = View.VISIBLE
                    submitButton.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                }
            }
        } else {
            Toast.makeText(context, "Error al actualizar el nivel: Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }



        // Aquí debes reemplazar con tus preguntas y opciones
        val questions = listOf(
            InformationBanner("Para jugar partidos competitivos en Playtronic, necesitas obtener tu nivel de juego.\n" + "\n" +
                    "Calcula tu nivel de juego Playtronic respondiendo a las siguientes preguntas con sinceridad."),
            Question("¿Cómo calificarías tu Derecha?", listOf(
                "No consigo poner en juego más de 4 bolas seguidas, gesto incompleto, sin controlar dirección pero me defiendo pasando bolas.",
                "Controlo dirección, soy muy fiable y fallo poco.",
                "Controlo tanto dirección como potencia y tengo una gran técnica.",
                "Mi Golpe de Derecha es mi arma principal, puedo cambiar la dirección y la potencia según lo necesite."
            )),
            Question("¿Cómo calificarías tu Revés?", listOf(
                "Suelo cometer errores frecuentes, tengo dificultad para controlar la dirección y la potencia.",
                "Tengo un buen control de la dirección y la potencia, pero a veces fallo en situaciones de presión.",
                "Mi Golpe de Revés es bastante sólido, puedo mantener intercambios prolongados sin cometer errores.",
                "Mi Golpe de Revés es una fortaleza, puedo cambiar la dirección y la potencia con facilidad."
            )),
            Question("¿Cómo calificarías tu Servicio?", listOf(
                "Mis saques rara vez pasan la red y tengo problemas para mantener un buen ritmo de servicio.",
                "Puedo colocar mi saque con precisión, pero mi potencia puede ser inconsistente.",
                "Mi servicio es confiable, puedo variar la dirección y la potencia según lo necesite.",
                "Tengo un gran servicio, puedo realizar saques potentes y precisos con facilidad."
            )),
            Question("¿Cómo calificarías tu Resto?", listOf(
                "Tengo dificultades para devolver servicios potentes y a menudo cometo errores no forzados.",
                "Puedo devolver servicios con precisión, pero a veces tengo problemas para generar potencia.",
                "Mi Resto es bastante sólido, puedo devolver la mayoría de los servicios con precisión y profundidad.",
                "Mi Resto es una fortaleza, puedo devolver servicios potentes con facilidad y colocación."
            )),
            Question("Califica tu Volea de Derecha", listOf(
                "Suelo cometer errores frecuentes, especialmente cuando tengo que moverme lateralmente.",
                "Tengo un buen control de la dirección, pero a veces fallo en las voleas de alta velocidad.",
                "Mi Volea de Derecha es bastante sólida, puedo ejecutarla con precisión y profundidad.",
                "Mi Volea de Derecha es una fortaleza, puedo terminar puntos rápidamente con ella."
            )),
            Question("Califica tu Volea de Revés", listOf(
                "Suelo evitar volear con el revés porque no me siento cómodo con él.",
                "Puedo ejecutar voleas de revés con precisión, pero a veces fallo bajo presión.",
                "Mi Volea de Revés es bastante confiable, puedo ejecutarla con precisión y profundidad.",
                "Mi Volea de Revés es una de mis mejores armas, puedo terminar puntos con ella."
            )),
            Question("Califica tu Remate", listOf(
                "Rara vez tengo la oportunidad de realizar remates, y cuando lo hago, a menudo fallo.",
                "Puedo realizar remates cuando la pelota viene a mi zona de confort, pero a veces fallo en situaciones complicadas.",
                "Mi Remate es bastante sólido, puedo terminar puntos con él cuando tengo la oportunidad.",
                "Mi Remate es una de mis mejores armas, puedo finalizar puntos con potencia y precisión."
            )),
            Question("Califica tu habilidad en los Rebotes", listOf(
                "Tengo dificultades para anticipar los rebotes y posicionarme correctamente para golpear la pelota.",
                "Puedo anticipar algunos rebotes y moverme adecuadamente, pero a veces me cuesta llegar a tiempo.",
                "Mi Habilidad en los Rebotes es bastante sólida, puedo anticipar la mayoría de los rebotes y moverme eficientemente.",
                "Tengo una gran habilidad en los rebotes, puedo anticipar y moverme con rapidez y precisión."
            )),
            Question("Califica tu Juego de Globos", listOf(
                "Suelo cometer errores frecuentes al intentar globos y a menudo no logro colocar la pelota donde quiero.",
                "Puedo ejecutar globos con cierta precisión, pero a veces carecen de profundidad o altura.",
                "Mi Juego de Globos es bastante sólido, puedo colocar la pelota con precisión y altura cuando lo necesito.",
                "Tengo un excelente Juego de Globos, puedo colocar la pelota exactamente donde quiero con profundidad y altura."
            )),
            Question("Califica tu Estrategia de Juego", listOf(
                "Tiendo a jugar de forma reactiva, sin un plan claro en mente.",
                "Puedo seguir un plan de juego básico, pero a veces me desvío de él bajo presión.",
                "Mi Estrategia de Juego es bastante sólida, puedo adaptarme a diferentes situaciones y seguir un plan con consistencia.",
                "Tengo una estrategia de juego avanzada y puedo anticipar los movimientos de mi oponente, controlando el ritmo del partido."
            ))
        )

        val questionAdapter = QuestionAdapter(questions)
        viewPager.adapter = questionAdapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        submitButton.setOnClickListener {
            // Aquí puedes obtener las respuestas y calcular el nivel del jugador
            if (questionAdapter.allQuestionsAnswered()) {
                val answers = questionAdapter.getAnswersIndices()
                val level = calculateLevel(answers)

                // Actualiza el nivel en Firebase
                val user = FirebaseAuth.getInstance().currentUser
                val twUser = user?.displayName
                val userEmail = user?.email
                val db = FirebaseFirestore.getInstance()

                if (userEmail != null) {
                    val googleDocRef = db.collection("users").document("(Google) $userEmail")
                    googleDocRef.get().addOnSuccessListener { googleDoc ->
                        if (googleDoc.exists()) {
                            googleDocRef.update("nivel", level).addOnSuccessListener {
                                // Haz que el CardView y el botón desaparezcan
                                view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                                submitButton.visibility = View.GONE
                                tabLayout.visibility = View.GONE
                            }
                        } else {
                            val emailDocRef = db.collection("users").document(userEmail)
                            emailDocRef.get().addOnSuccessListener { emailDoc ->
                                if (emailDoc.exists()) {
                                    emailDocRef.update("nivel", level).addOnSuccessListener {
                                        // Haz que el CardView y el botón desaparezcan
                                        view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                                        submitButton.visibility = View.GONE
                                        tabLayout.visibility = View.GONE
                                    }
                                } else {
                                    if (twUser != null) {
                                        val twitterDocRef = db.collection("users").document("(Twitter) $twUser")
                                        twitterDocRef.get().addOnSuccessListener { twitterDoc ->
                                            if (twitterDoc.exists()) {
                                                twitterDocRef.update("nivel", level).addOnSuccessListener {
                                                    // Haz que el CardView y el botón desaparezcan
                                                    view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                                                    submitButton.visibility = View.GONE
                                                    tabLayout.visibility = View.GONE
                                                }
                                            } else {
                                                Toast.makeText(context, "Error al actualizar el nivel: Documento no encontrado", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Error al actualizar el nivel: Usuario no autenticado", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (twUser != null) {
                        val twitterDocRef = db.collection("users").document("(Twitter) $twUser")
                        twitterDocRef.get().addOnSuccessListener { twitterDoc ->
                            if (twitterDoc.exists()) {
                                twitterDocRef.update("nivel", level).addOnSuccessListener {
                                    // Haz que el CardView y el botón desaparezcan
                                    view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                                    submitButton.visibility = View.GONE
                                    tabLayout.visibility = View.GONE
                                }
                            } else {
                                Toast.makeText(context, "Error al actualizar el nivel: Documento no encontrado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Error al actualizar el nivel: Usuario no autenticado", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Debes contestar todas las preguntas para poder calcular tu nivel Playtronic.", Toast.LENGTH_SHORT).show()
            }
        }



    }


    private fun calculateLevel(answers: List<Int>): Double {
        var total = 0.0
        for (answer in answers) {
            total += when (answer) {
                0 -> 0.25
                1 -> 0.50
                2 -> 0.75
                3 -> 1.0
                else -> 0.0
            }
        }
        return total
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