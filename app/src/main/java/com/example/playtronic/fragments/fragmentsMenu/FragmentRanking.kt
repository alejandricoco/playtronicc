package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.ParseException
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager.TAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playtronic.Player
import com.example.playtronic.R
import com.example.playtronic.Resultado
import com.example.playtronic.ResultadoAdapter
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentRanking : Fragment() {

    private lateinit var tenisPadelACTV: AutoCompleteTextView
    private lateinit var fechaInput: TextInputEditText
    private lateinit var victoriaDerrotaACTV: AutoCompleteTextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ranking, container, false)

        // Set visibility of other views to GONE initially
        val spinnerIds = arrayOf(R.id.reSet1_1, R.id.reSet1_2, R.id.reSet2_1, R.id.reSet2_2, R.id.reSet3_1, R.id.reSet3_2)
        val otherViews = arrayOf(R.id.tilTenisPadel, R.id.fecha, R.id.tilVictoriaDerrota, R.id.set1, R.id.set2, R.id.set3) + spinnerIds
        for (id in otherViews) {
            view.findViewById<View>(id).visibility = View.GONE
        }

        tenisPadelACTV = view.findViewById(R.id.actTenisPadel)
        victoriaDerrotaACTV = view.findViewById(R.id.actVictoriaDerrota)
        fechaInput = view.findViewById(R.id.inputFecha)


        return view
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Crear un ArrayAdapter con los números del 1 al 7
        val numbers = arrayOf("-", "0", "1", "2", "3", "4", "5", "6", "7")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, numbers)
        // Aplicar el adapter al spinner
        val spinnerIds = arrayOf(R.id.reSet1_1, R.id.reSet1_2, R.id.reSet2_1, R.id.reSet2_2, R.id.reSet3_1, R.id.reSet3_2)
        for (id in spinnerIds) {
            val spinner: Spinner = view.findViewById(id)
            spinner.adapter = adapter
        }

        val otherViews = arrayOf(R.id.btnHecho, R.id.tilTenisPadel, R.id.fecha, R.id.tilVictoriaDerrota, R.id.set1, R.id.set2, R.id.set3) + spinnerIds
        for (id in otherViews) {
            view.findViewById<View>(id).visibility = View.GONE
            view.findViewById<Button>(R.id.btnSubirResultadoVerResultados).visibility = View.GONE
        }

        // Configurar el toggle group
        val toggleGroup: MaterialButtonToggleGroup = view.findViewById(R.id.toggleGroupRankingResultados)
        val recyclerViewRanking: RecyclerView = view.findViewById(R.id.recyclerViewRanking)
        recyclerViewRanking.layoutManager = LinearLayoutManager(context)
        toggleGroup.check(R.id.btnRanking)
        recyclerViewRanking.visibility = View.VISIBLE


        // Obtener una referencia a la base de datos de Firebase
        val db = FirebaseFirestore.getInstance()

        // Obtener el nombre de usuario actual
        val user = FirebaseAuth.getInstance().currentUser
        val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val username = user?.displayName ?: sharedPreferences.getString("nombreUsuario", "Default")

        // Obtener todos los resultados del usuario actual
        db.collection("resultados")
            .get()
            .addOnSuccessListener { documents ->
                // Crear un mapa para almacenar los datos de cada jugador
                val playersData = mutableMapOf<String, Player>()

                // Iterar sobre cada documento (resultado)
                documents.forEach { document ->
                    val username = document.getString("nombre") ?: document.getString("usuario")!!

                    // Si el jugador aún no está en el mapa, añadirlo
                    if (!playersData.containsKey(username)) {
                        playersData[username] = Player(username, 0, 0, 0, 0)
                    }

                    // Obtener el objeto Player del mapa
                    val player = playersData[username]!!

                    // Incrementar PJ
                    player.PJ++

                    // Incrementar PG o PP dependiendo del resultado
                    if (document.getString("winlose") == "Victoria") {
                        player.PG++
                    } else if (document.getString("winlose") == "Derrota") {
                        player.PP++
                    }

                    // Calcular Pts
                    val potentialPts = player.PG * 10 - player.PP * 3
                    player.Pts = if (potentialPts > 0) potentialPts else 0
                }

                val players = playersData.values.toList().sortedByDescending { it.Pts }

                // Pasar los datos al RankingAdapter y establecerlo en el RecyclerView
                val rankingAdapter = RankingAdapter(players)
                recyclerViewRanking.adapter = rankingAdapter
            }


        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) { // Solo cambiar la visibilidad cuando un botón se selecciona
                if (checkedId == R.id.btnRanking) {
                    recyclerViewRanking.visibility = View.VISIBLE
                    view.findViewById<RecyclerView>(R.id.recyclerViewResultados).visibility = View.GONE
                    view.findViewById<Button>(R.id.btnSubirResultadoVerResultados).visibility = View.GONE
                    for (id in otherViews) {
                        view.findViewById<View>(id).visibility = View.GONE
                    }

                    // Obtener todos los resultados del usuario actual
                    db.collection("resultados")
                        .get()
                        .addOnSuccessListener { documents ->
                            // Crear un mapa para almacenar los datos de cada jugador
                            val playersData = mutableMapOf<String, Player>()

                            // Iterar sobre cada documento (resultado)
                            documents.forEach { document ->
                                val username = document.getString("nombre") ?: document.getString("usuario")!!

                                // Si el jugador aún no está en el mapa, añadirlo
                                if (!playersData.containsKey(username)) {
                                    playersData[username] = Player(username, 0, 0, 0, 0)
                                }

                                // Obtener el objeto Player del mapa
                                val player = playersData[username]!!

                                // Incrementar PJ
                                player.PJ++

                                // Incrementar PG o PP dependiendo del resultado
                                if (document.getString("winlose") == "Victoria") {
                                    player.PG++
                                } else if (document.getString("winlose") == "Derrota") {
                                    player.PP++
                                }

                                // Calcular Pts
                                val potentialPts = player.PG * 10 - player.PP * 3
                                player.Pts = if (potentialPts > 0) potentialPts else 0
                            }

                            val players = playersData.values.toList().sortedByDescending { it.Pts }

                            // Pasar los datos al RankingAdapter y establecerlo en el RecyclerView
                            val rankingAdapter = RankingAdapter(players)
                            recyclerViewRanking.adapter = rankingAdapter
                        }

                } else if (checkedId == R.id.btnResultados) {
                    recyclerViewRanking.visibility = View.GONE
                    view.findViewById<Button>(R.id.btnSubirResultadoVerResultados).visibility = View.VISIBLE
                    for (id in otherViews) {
                        view.findViewById<View>(id).visibility = View.VISIBLE
                    }
                }
            }
        }


        // Configurar los auto complete text views
        val tenisPadelOptions = arrayOf("Tenis", "Padel")
        val victoriaDerrotaOptions = arrayOf("Victoria", "Derrota")

        tenisPadelACTV.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, tenisPadelOptions))
        victoriaDerrotaACTV.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, victoriaDerrotaOptions))

        tenisPadelACTV.inputType = InputType.TYPE_NULL
        victoriaDerrotaACTV.inputType = InputType.TYPE_NULL

        tenisPadelACTV.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as? AutoCompleteTextView)?.showDropDown()
            }
        }

        victoriaDerrotaACTV.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                (v as? AutoCompleteTextView)?.showDropDown()
            }
        }

        // Configurar el date picker
        fechaInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                fechaInput.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day).show()
        }


        val btnSubirResultado = view.findViewById<Button>(R.id.btnSubirResultadoVerResultados)
        btnSubirResultado.setOnClickListener {
            val isViewingResults = btnSubirResultado.text.toString() == "Ver mis resultados"
            if (isViewingResults) {
                // Cambiar el texto del botón a "Subir Resultado"
                btnSubirResultado.text = "Subir Resultado"
                // Ocultar los otros elementos de la vista

                for (id in otherViews) {
                    view.findViewById<View>(id).visibility = View.GONE
                }
                // Mostrar el RecyclerView de Resultados
                val recyclerViewResultados: RecyclerView = view.findViewById(R.id.recyclerViewResultados)
                recyclerViewResultados.layoutManager = LinearLayoutManager(context)
                recyclerViewResultados.visibility = View.VISIBLE


                val user = FirebaseAuth.getInstance().currentUser
                val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
                val username = user?.displayName ?: sharedPreferences.getString("nombreUsuario", "Default")
                val email  = user?.email
                val db = FirebaseFirestore.getInstance()
                GlobalScope.launch(Dispatchers.Main) {
                    val nombre = getUserNameByEmail(db, email ?: "")
                    // Aquí puedes usar 'nombre'


                db.collection("resultados")
                    .whereEqualTo("usuario", nombre ?: username)
                    .get()
                    .addOnSuccessListener { documents ->
                        val resultados = documents.mapNotNull { document ->
                            val fechaTimestamp = document.getTimestamp("fecha")
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val fechaString = fechaTimestamp?.let { sdf.format(it.toDate()) }
                            if (fechaString != null) {
                                Resultado(
                                    document.getString("deporte")!!,
                                    fechaString,
                                    document.getString("set1_1")!!,
                                    document.getString("set1_2")!!,
                                    document.getString("set2_1")!!,
                                    document.getString("set2_2")!!,
                                    document.getString("set3_1")!!,
                                    document.getString("set3_2")!!,
                                    document.getString("usuario")!!,
                                    document.getString("winlose")!!
                                )
                            } else {
                                null
                            }
                        }.sortedByDescending {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            sdf.parse(it.fecha)
                        }

                        val adapter = ResultadoAdapter(resultados)
                        recyclerViewResultados.adapter = adapter
                    }

                }

            } else {
                // Cambiar el texto del botón a "Ver mis resultados"
                btnSubirResultado.text = "Ver mis resultados"
                // Mostrar los otros elementos de la vista
                for (id in otherViews) {
                    view.findViewById<View>(id).visibility = View.VISIBLE
                }
                // Ocultar el RecyclerView
                val recyclerViewResultados: RecyclerView = view.findViewById(R.id.recyclerViewResultados)
                recyclerViewResultados.visibility = View.GONE
            }
        }

        val btnGuardarResultado = view.findViewById<Button>(R.id.btnHecho)
        btnGuardarResultado.setOnClickListener {

            val user = FirebaseAuth.getInstance().currentUser
            val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
            val username = user?.displayName ?: sharedPreferences.getString("nombreUsuario", "Default")
            val userEmail = user?.email
            val twUser = user?.displayName
            val db = FirebaseFirestore.getInstance()

            // Comprobar si el usuario tiene un nivel
            if (userEmail != null) {
                val googleDocRef = db.collection("users").document("(Google) $userEmail")
                googleDocRef.get().addOnSuccessListener { googleDoc ->
                    if (googleDoc.exists() && googleDoc.getDouble("nivel") != null) {
                        guardarResultado(db, username!!)
                    } else {
                        val emailDocRef = db.collection("users").document(userEmail)
                        emailDocRef.get().addOnSuccessListener { emailDoc ->
                            if (emailDoc.exists() && emailDoc.getDouble("nivel") != null) {
                                val usuarioEmail = emailDoc.getString("nombre")
                                guardarResultado(db, usuarioEmail!!)
                            } else {
                                Toast.makeText(context, "Para poder subir un resultado necesitas obtener Nivel Playtronic", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            } else if (twUser != null) {
                val twitterDocRef = db.collection("users").document("(Twitter) $twUser")
                twitterDocRef.get().addOnSuccessListener { twitterDoc ->
                    if (twitterDoc.exists() && twitterDoc.getDouble("nivel") != null) {
                        guardarResultado(db, username!!)
                    } else {
                        Toast.makeText(context, "Para poder subir un resultado necesitas obtener Nivel Playtronic", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Error al actualizar el nivel: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            }
        }

        // Aquí debes obtener la lista de jugadores desde Firestore
        val players = listOf<Player>()
        val rvRanking = view.findViewById<RecyclerView>(R.id.recyclerViewRanking)
        rvRanking.adapter = RankingAdapter(players)


    }

    @SuppressLint("RestrictedApi")
    fun guardarResultado(db: FirebaseFirestore, username: String) {
        // Obtener los datos de los campos de entrada
        val deporte = tenisPadelACTV.text.toString()
        val fechaString = fechaInput.text.toString()
        val winlose = victoriaDerrotaACTV.text.toString()
        val usuario = username
        val set1_1 = view?.findViewById<Spinner>(R.id.reSet1_1)?.selectedItem.toString()
        val set1_2 = view?.findViewById<Spinner>(R.id.reSet1_2)?.selectedItem.toString()
        val set2_1 = view?.findViewById<Spinner>(R.id.reSet2_1)?.selectedItem.toString()
        val set2_2 = view?.findViewById<Spinner>(R.id.reSet2_2)?.selectedItem.toString()
        val set3_1 = view?.findViewById<Spinner>(R.id.reSet3_1)?.selectedItem.toString()
        val set3_2 = view?.findViewById<Spinner>(R.id.reSet3_2)?.selectedItem.toString()

        // Comprobar que los campos requeridos estén completos
        if (deporte.isEmpty() || fechaString.isEmpty() || winlose.isEmpty() || set1_1 == "-" || set1_2 == "-" || set2_1 == "-" || set2_2 == "-" ) {
            Toast.makeText(context, "Por favor, completa todos los campos requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        //Parseamos la fecha
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fecha: Date
        try {
            fecha = sdf.parse(fechaString)
        } catch (e: ParseException) {
            Toast.makeText(context, "Fecha inválida. Por favor, ingresa una fecha válida.", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear un nuevo objeto con los datos
        val resultado = hashMapOf(
            "deporte" to deporte,
            "fecha" to Timestamp(fecha),
            "winlose" to winlose,
            "usuario" to usuario,
            "set1_1" to set1_1,
            "set1_2" to set1_2,
            "set2_1" to set2_1,
            "set2_2" to set2_2,
            "set3_1" to set3_1,
            "set3_2" to set3_2
        )

        // Agregar un nuevo documento a la colección "resultados"
        db.collection("resultados")
            .add(resultado)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(context, "Resultado subido con éxito", Toast.LENGTH_LONG).show()

                // Obtener el correo electrónico del usuario
                val userEmail = FirebaseAuth.getInstance().currentUser?.email
                val twUser = FirebaseAuth.getInstance().currentUser?.displayName

                // Comprobar si el correo electrónico del usuario no es nulo
                if (userEmail != null) {
                    // Obtener el documento del usuario
                    val googleDocRef = db.collection("users").document("(Google) $userEmail")
                    googleDocRef.get().addOnSuccessListener { googleDoc ->
                        if (googleDoc.exists() && googleDoc.getDouble("nivel") != null) {
                            updateNivel(db, "(Google) $userEmail", winlose)
                        } else {
                            val emailDocRef = db.collection("users").document(userEmail)
                            emailDocRef.get().addOnSuccessListener { emailDoc ->
                                if (emailDoc.exists() && emailDoc.getDouble("nivel") != null) {
                                    updateNivel(db, userEmail, winlose)
                                }
                            }
                        }
                    }
                } else if (twUser != null) {
                    val twitterDocRef = db.collection("users").document("(Twitter) $twUser")
                    twitterDocRef.get().addOnSuccessListener { twitterDoc ->
                        if (twitterDoc.exists() && twitterDoc.getDouble("nivel") != null) {
                            updateNivel(db, "(Twitter) $twUser", winlose)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    @SuppressLint("RestrictedApi")
    fun updateNivel(db: FirebaseFirestore, docId: String, winlose: String) {
        db.collection("users").document(docId)
            .get()
            .addOnSuccessListener { document ->
                val nivel = document.getDouble("nivel") ?: 0.0

                // Actualizar el nivel dependiendo del resultado
                var nuevoNivel = if (winlose == "Victoria") nivel + 0.3 else nivel - 0.1

                // Formatear el nuevo nivel usando Locale.US para asegurar el punto decimal
                nuevoNivel = String.format(Locale.US, "%.1f", nuevoNivel).toDouble()

                // Obtener la lista de niveles actual
                val listaNiveles = document.get("listaNiveles") as? ArrayList<Double> ?: arrayListOf()

                // Añadir el nuevo nivel a la lista
                listaNiveles.add(nuevoNivel)

                // Actualizar el nivel y la lista de niveles en la base de datos
                db.collection("users").document(docId)
                    .set(mapOf("nivel" to nuevoNivel, "listaNiveles" to listaNiveles), SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "Nivel y lista de niveles actualizados con éxito")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error al actualizar el nivel y la lista de niveles", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al obtener el documento del usuario", e)
            }
    }


    suspend fun getUserNameByEmail(db: FirebaseFirestore, email: String): String? = withContext(Dispatchers.IO) {
        if (email.isNotBlank()) {
            val docRef = db.collection("users").document(email)
            val document = docRef.get().await()
            if (document != null && document.exists()) {
                document.getString("nombre")
            } else {
                null
            }
        } else {
            null
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