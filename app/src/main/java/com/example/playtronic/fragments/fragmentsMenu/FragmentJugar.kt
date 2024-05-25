package com.example.playtronic.fragments.fragmentsMenu

import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.playtronic.*
import com.google.android.material.button.MaterialButtonToggleGroup
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

                    // No es necesario mostrar el formulario pq el usuario ya ha calculado su nivel...
                    view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                    submitButton.visibility = View.GONE
                    tabLayout.visibility = View.GONE

                    // aqui va la funcion frgNivelExitoso
                    frgNivelExitoso()




                } else {
                    val emailDocRef = db.collection("users").document(userEmail)
                    emailDocRef.get().addOnSuccessListener { emailDoc ->
                        if (emailDoc.exists() && emailDoc.getDouble("nivel") != null) {

                            // No es necesario mostrar el formulario pq el usuario ya ha calculado su nivel...
                            view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                            submitButton.visibility = View.GONE
                            tabLayout.visibility = View.GONE

                            // aqui va la funcion frgNivelExitoso
                            frgNivelExitoso()



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

                        // No es necesario mostrar el formulario pq el usuario ya ha calculado su nivel...
                        view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                        submitButton.visibility = View.GONE
                        tabLayout.visibility = View.GONE

                        // aqui va la funcion frgNivelExitoso
                        frgNivelExitoso()




                    } else {
                        // El nivel es null, por lo que mostramos formulario
                        view.findViewById<CardView>(R.id.cardView).visibility = View.VISIBLE
                        submitButton.visibility = View.VISIBLE
                        tabLayout.visibility = View.VISIBLE
                    }
                } else {
                    // El documento no existe y mostramos el formulario
                    view.findViewById<CardView>(R.id.cardView).visibility = View.VISIBLE
                    submitButton.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                }
            }
        } else {
            Toast.makeText(context, "Error al actualizar el nivel: Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }


        val questions = listOf(
            InformationBanner("Para jugar partidos competitivos en Playtronic, necesitas obtener tu nivel de juego.\n" + "\n" +
                    "Calcula tu nivel de juego Playtronic respondiendo a las siguientes preguntas con sinceridad."),
            Question("¿Cómo calificarías tu Derecha?", listOf(
                "\nNo consigo poner en juego más de 4 bolas seguidas, gesto incompleto, sin controlar dirección pero me defiendo pasando bolas.\n",
                "\nControlo dirección, soy muy fiable y fallo poco.\n",
                "\nControlo tanto dirección como potencia y tengo una gran técnica.\n",
                "\nMi Golpe de Derecha es mi arma principal, puedo cambiar la dirección y la potencia según lo necesite.\n"
            )),
            Question("¿Cómo calificarías tu Revés?", listOf(
                "\nSuelo cometer errores frecuentes, tengo dificultad para controlar la dirección y la potencia.\n",
                "\nTengo un buen control de la dirección y la potencia, pero a veces fallo en situaciones de presión.\n",
                "\nMi Golpe de Revés es bastante sólido, puedo mantener intercambios prolongados sin cometer errores.\n",
                "\nMi Golpe de Revés es una fortaleza, puedo cambiar la dirección y la potencia con facilidad.\n"
            )),
            Question("¿Cómo calificarías tu Servicio?", listOf(
                "\nMis saques rara vez pasan la red y tengo problemas para mantener un buen ritmo de servicio.\n",
                "\nPuedo colocar mi saque con precisión, pero mi potencia puede ser inconsistente.\n",
                "\nMi servicio es confiable, puedo variar la dirección y la potencia según lo necesite.\n",
                "\nTengo un gran servicio, puedo realizar saques potentes y precisos con facilidad.\n"
            )),
            Question("¿Cómo calificarías tu Resto?", listOf(
                "\nTengo dificultades para devolver servicios potentes y a menudo cometo errores no forzados.\n",
                "\nPuedo devolver servicios con precisión, pero a veces tengo problemas para generar potencia.\n",
                "\nMi Resto es bastante sólido, puedo devolver la mayoría de los servicios con precisión y profundidad.\n",
                "\nMi Resto es una fortaleza, puedo devolver servicios potentes con facilidad y colocación.\n"
            )),
            Question("Califica tu Volea de Derecha", listOf(
                "\nSuelo cometer errores frecuentes, especialmente cuando tengo que moverme lateralmente.\n",
                "\nTengo un buen control de la dirección, pero a veces fallo en las voleas de alta velocidad.\n",
                "\nMi Volea de Derecha es bastante sólida, puedo ejecutarla con precisión y profundidad.\n",
                "\nMi Volea de Derecha es una fortaleza, puedo terminar puntos rápidamente con ella.\n"
            )),
            Question("Califica tu Volea de Revés", listOf(
                "\nSuelo evitar volear con el revés porque no me siento cómodo con él.\n",
                "\nPuedo ejecutar voleas de revés con precisión, pero a veces fallo bajo presión.\n",
                "\nMi Volea de Revés es bastante confiable, puedo ejecutarla con precisión y profundidad.\n",
                "\nMi Volea de Revés es una de mis mejores armas, puedo terminar puntos con ella.\n"
            )),
            Question("Califica tu Remate", listOf(
                "\nRara vez tengo la oportunidad de realizar remates, y cuando lo hago, a menudo fallo.\n",
                "\nPuedo realizar remates cuando la pelota viene a mi zona de confort, pero a veces fallo en situaciones complicadas.\n",
                "\nMi Remate es bastante sólido, puedo terminar puntos con él cuando tengo la oportunidad.\n",
                "\nMi Remate es una de mis mejores armas, puedo finalizar puntos con potencia y precisión.\n"
            )),
            Question("Califica tu habilidad en los Rebotes", listOf(
                "\nTengo dificultades para anticipar los rebotes y posicionarme correctamente para golpear la pelota.\n",
                "\nPuedo anticipar algunos rebotes y moverme adecuadamente, pero a veces me cuesta llegar a tiempo.\n",
                "\nMi Habilidad en los Rebotes es bastante sólida, puedo anticipar la mayoría de los rebotes y moverme eficientemente.\n",
                "\nTengo una gran habilidad en los rebotes, puedo anticipar y moverme con rapidez y precisión.\n"
            )),
            Question("Califica tu Juego de Globos", listOf(
                "\nSuelo cometer errores frecuentes al intentar globos y a menudo no logro colocar la pelota donde quiero.\n",
                "\nPuedo ejecutar globos con cierta precisión, pero a veces carecen de profundidad o altura.\n",
                "\nMi Juego de Globos es bastante sólido, puedo colocar la pelota con precisión y altura cuando lo necesito.\n",
                "\nTengo un excelente Juego de Globos, puedo colocar la pelota exactamente donde quiero con profundidad y altura.\n"
            )),
            Question("Califica tu Estrategia de Juego", listOf(
                "\nTiendo a jugar de forma reactiva, sin un plan claro en mente.\n",
                "\nPuedo seguir un plan de juego básico, pero a veces me desvío de él bajo presión.\n",
                "\nMi Estrategia de Juego es bastante sólida, puedo adaptarme a diferentes situaciones y seguir un plan con consistencia.\n",
                "\nTengo una estrategia de juego avanzada y puedo anticipar los movimientos de mi oponente, controlando el ritmo del partido.\n"
            ))
        )

        val questionAdapter = QuestionAdapter(questions)
        viewPager.adapter = questionAdapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        submitButton.setOnClickListener {
            if (questionAdapter.allQuestionsAnswered()) {
                val answers = questionAdapter.getAnswersIndices()
                val level = calcularNivel(answers)

                // Actualizamos el nivel en Firebase
                val user = FirebaseAuth.getInstance().currentUser
                val twUser = user?.displayName
                val userEmail = user?.email
                val db = FirebaseFirestore.getInstance()

                if (userEmail != null) {
                    val googleDocRef = db.collection("users").document("(Google) $userEmail")
                    googleDocRef.get().addOnSuccessListener { googleDoc ->
                        if (googleDoc.exists()) {
                            googleDocRef.update("nivel", level).addOnSuccessListener {
                                // El cardview y el botón desaparecen
                                view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                                submitButton.visibility = View.GONE
                                tabLayout.visibility = View.GONE
                                cargarFragment(FragmentNivelSuccess())
                            }
                        } else {
                            val emailDocRef = db.collection("users").document(userEmail)
                            emailDocRef.get().addOnSuccessListener { emailDoc ->
                                if (emailDoc.exists()) {
                                    emailDocRef.update("nivel", level).addOnSuccessListener {
                                        // El cardview y el botón desaparecen
                                        view.findViewById<CardView>(R.id.cardView).visibility = View.GONE
                                        submitButton.visibility = View.GONE
                                        tabLayout.visibility = View.GONE
                                        cargarFragment(FragmentNivelSuccess())
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
                                                    cargarFragment(FragmentNivelSuccess())
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
                                    cargarFragment(FragmentNivelSuccess())
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


    private fun calcularNivel(answers: List<Int>): Double {
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


    private fun frgNivelExitoso() {
        // Hacer visible el toggle y el RecyclerView
        val toggleGroupCrearUnirmeA = view?.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroupCrearUnirmeA)
        val tvUnPartidoDe = view?.findViewById<TextView>(R.id.tvUnPartidoDe)
        val toggleGroupDeporte = view?.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroupDeporte)

        toggleGroupCrearUnirmeA?.clearChecked()
        toggleGroupDeporte?.clearChecked()

        // VARIABLES CUANDO LA OPCION ES UNIRSE A UN PARTIDO
        val recyclerViewPartidos = view?.findViewById<RecyclerView>(R.id.recyclerViewPartidos)
        recyclerViewPartidos?.layoutManager = LinearLayoutManager(context)

        // VARIABLES CUANDO LA OPCION ES CREAR UN PARTIDO
        val tilHorarioPreferido = view?.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilHorarioPreferido)
        val actHorarioPreferido = view?.findViewById<AutoCompleteTextView>(R.id.actHorarioPreferido)
        val tilNivelOponente = view?.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tilNivelOponente)
        val actNivelOponente = view?.findViewById<AutoCompleteTextView>(R.id.actNivelOponente)

        val itemsNivelesOponente = listOf("Casi malo (1 - 3.4)", "Casi bueno (3.5 - 7.0)", "Leyenda (7.1 - 10.0)", "No me importa el nivel")
        val adapterNivelesOponente = ArrayAdapter(requireContext(), R.layout.list_item, itemsNivelesOponente)
        (actNivelOponente as? AutoCompleteTextView)?.setAdapter(adapterNivelesOponente)

        val itemsHorarioPreferido = listOf("Mañana", "Tarde", "Mañana o tarde")
        val adapterHorarioPreferido = ArrayAdapter(requireContext(), R.layout.list_item, itemsHorarioPreferido)
        (actHorarioPreferido as? AutoCompleteTextView)?.setAdapter(adapterHorarioPreferido)

        actHorarioPreferido?.setInputType(InputType.TYPE_NULL)
        actNivelOponente?.setInputType(InputType.TYPE_NULL)

        actHorarioPreferido?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as? AutoCompleteTextView)?.showDropDown()
            }
        }

        actNivelOponente?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as? AutoCompleteTextView)?.showDropDown()
            }
        }

        val btnCrearPartido = view?.findViewById<Button>(R.id.btnCrearPartido)

        // Set visibility
        toggleGroupCrearUnirmeA?.visibility = View.VISIBLE
        tvUnPartidoDe?.visibility = View.VISIBLE
        toggleGroupDeporte?.visibility = View.VISIBLE
        recyclerViewPartidos?.visibility = View.GONE
        tilHorarioPreferido?.visibility = View.GONE
        actHorarioPreferido?.visibility = View.GONE
        tilNivelOponente?.visibility = View.GONE
        actNivelOponente?.visibility = View.GONE
        btnCrearPartido?.visibility = View.GONE

        // Añadimos los listeners a los botones
        view?.findViewById<Button>(R.id.btnCrear)?.setOnClickListener {
            // Show the fields for creating a game
            tilHorarioPreferido?.visibility = View.VISIBLE
            actHorarioPreferido?.visibility = View.VISIBLE
            tilNivelOponente?.visibility = View.VISIBLE
            actNivelOponente?.visibility = View.VISIBLE
            btnCrearPartido?.visibility = View.VISIBLE
            toggleGroupDeporte?.clearChecked()

            // Hide the RecyclerView
            recyclerViewPartidos?.visibility = View.GONE
        }

        view?.findViewById<Button>(R.id.btnUnirmeA)?.setOnClickListener {
            // Show the RecyclerView
            recyclerViewPartidos?.visibility = View.VISIBLE

            // Hide the fields for creating a game
            tilHorarioPreferido?.visibility = View.GONE
            actHorarioPreferido?.visibility = View.GONE
            tilNivelOponente?.visibility = View.GONE
            actNivelOponente?.visibility = View.GONE
            btnCrearPartido?.visibility = View.GONE
            toggleGroupDeporte?.clearChecked()

            // AQUI MOSTRAMOS LOS PARTIDOS DISPONIBLES DESDE FIREBASE
            val db = FirebaseFirestore.getInstance()
            db.collection("partidos").get().addOnSuccessListener { result ->
                val partidos = result.map { document ->
                    Partido(
                        id = document.id,
                        creadoPor = document.getString("creadoPor") ?: "",
                        deporte = document.getString("deporte") ?: "",
                        horarioPreferido = document.getString("horarioPreferido") ?: "",
                        nivelOponente = document.getString("nivelOponente") ?: ""
                    )
                }.filter { partido ->
                    // Filtra los partidos creados por el usuario actual
                    val user = FirebaseAuth.getInstance().currentUser
                    val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
                    //Si el usuario firebase es nulo, se obtiene el nombre de usuario guardado en SharedPreferences
                    val username = user?.displayName ?: sharedPreferences.getString("nombreUsuario", "Default")
                    partido.creadoPor != username
                }

                // Configura el RecyclerView
                recyclerViewPartidos?.adapter = PartidoAdapter(partidos)
            }
        }

        //AQUI FILTRAMOS POR PADEL O TENIS LOS PARTIDOS DEL RECYCLERVIEW

        toggleGroupDeporte?.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val selectedSport = when (checkedId) {
                    R.id.btnTenis -> "Tenis"
                    R.id.btnPadel -> "Padel"
                    else -> ""
                }

                // AQUI MOSTRAMOS LOS PARTIDOS DISPONIBLES DESDE FIREBASE FILTRADOS POR DEPORTE
                val db = FirebaseFirestore.getInstance()
                db.collection("partidos").get().addOnSuccessListener { result ->
                    val partidos = result.map { document ->
                        Partido(
                            id = document.id,
                            creadoPor = document.getString("creadoPor") ?: "",
                            deporte = document.getString("deporte") ?: "",
                            horarioPreferido = document.getString("horarioPreferido") ?: "",
                            nivelOponente = document.getString("nivelOponente") ?: ""
                        )
                    }.filter { partido ->
                        // Filtra los partidos creados por el usuario actual
                        val user = FirebaseAuth.getInstance().currentUser
                        val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
                        //Si el usuario firebase es nulo, se obtiene el nombre de usuario guardado en SharedPreferences
                        val username = user?.displayName ?: sharedPreferences.getString("nombreUsuario", "Default")
                        partido.creadoPor != username && partido.deporte == selectedSport
                    }

                    // Configura el RecyclerView
                    recyclerViewPartidos?.adapter = PartidoAdapter(partidos)
                }
            }
        }

        // Add listener to the create game button
        btnCrearPartido?.setOnClickListener {
            // Validate the input fields
            if (actHorarioPreferido?.text?.isNotEmpty() == true &&
                actNivelOponente?.text?.isNotEmpty() == true) {

                //Obtenemos el usuario actual
                val user = FirebaseAuth.getInstance().currentUser
                val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
                //Si el usuario firebase es nulo, se obtiene el nombre de usuario guardado en SharedPreferences
                val username = user?.displayName ?: sharedPreferences.getString("nombreUsuario", "Default")

                // Get the selected sport
                val selectedSportId = toggleGroupDeporte?.checkedButtonId ?: -1
                val selectedSport = when (selectedSportId) {
                    R.id.btnTenis -> "Tenis"
                    R.id.btnPadel -> "Padel"
                    else -> ""
                }

                // Get the opponent level
                val nivelOponente = when (actNivelOponente?.text.toString()) {
                    "Casi malo (1 - 3.4)" -> "Casi malo (1 - 3.4)"
                    "Casi bueno (3.5 - 7.0)" -> "Casi bueno (3.5 - 7.0)"
                    "Leyenda (7.1 - 10.0)" -> "Leyenda (7.1 - 10.0)"
                    "No me importa el nivel" -> "Cualquier nivel"
                    else -> ""
                }

                val horarioPreferido = when (actHorarioPreferido?.text.toString()) {
                    "Mañana" -> "Mañana"
                    "Tarde" -> "Tarde"
                    "Mañana o tarde" -> "Mañana o tarde"
                    else -> ""
                }

                // Create a new game
                val nuevoPartido = hashMapOf(
                    "creadoPor" to username,
                    "deporte" to selectedSport,
                    "horarioPreferido" to horarioPreferido,
                    "nivelOponente" to nivelOponente,
                    "contador" to 0
                )

                // Save the new game to Firebase
                val db = FirebaseFirestore.getInstance()
                db.collection("partidos")
                    .add(nuevoPartido)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Partido creado con éxito", Toast.LENGTH_SHORT).show()
                        val id = it.id
                        //AQUI TENEMOS EL ID DEL PARTIDO CREADO NUEVO
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al crear el partido: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun cargarFragment(fragment: Fragment) {
        // Asegúrate de que la actividad contenedora no sea nula
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.drawer_layout, fragment)
        fragmentTransaction.addToBackStack(null) // Permite volver al FragmentLogin al presionar el botón atrás
        fragmentTransaction.commit()
    }


}