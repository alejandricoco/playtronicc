package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FragmentRanking : Fragment() {


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
                    val username = document.getString("usuario")!!

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
                    player.Pts = player.PG * 10 - player.PP * 3
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
        val tenisPadelACTV: AutoCompleteTextView = view.findViewById(R.id.actTenisPadel)
        val victoriaDerrotaACTV: AutoCompleteTextView = view.findViewById(R.id.actVictoriaDerrota)
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
        val fechaInput: TextInputEditText = view.findViewById(R.id.inputFecha)
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
                val db = FirebaseFirestore.getInstance()
                db.collection("resultados")
                    .whereEqualTo("usuario", username)
                    .get()
                    .addOnSuccessListener { documents ->
                        val resultados = documents.map { document ->
                            Resultado(
                                document.getString("deporte")!!,
                                document.getString("fecha")!!,
                                document.getString("set1_1")!!,
                                document.getString("set1_2")!!,
                                document.getString("set2_1")!!,
                                document.getString("set2_2")!!,
                                document.getString("set3_1")!!,
                                document.getString("set3_2")!!,
                                document.getString("usuario")!!,
                                document.getString("winlose")!!
                            )
                        }.sortedByDescending { it.fecha }

                        val adapter = ResultadoAdapter(resultados)
                        recyclerViewResultados.adapter = adapter
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
            // Obtener los datos de los campos de entrada
            val deporte = tenisPadelACTV.text.toString()
            val fecha = fechaInput.text.toString()
            val winlose = victoriaDerrotaACTV.text.toString()
            val usuario = username
            val set1_1 = view.findViewById<Spinner>(R.id.reSet1_1).selectedItem.toString()
            val set1_2 = view.findViewById<Spinner>(R.id.reSet1_2).selectedItem.toString()
            val set2_1 = view.findViewById<Spinner>(R.id.reSet2_1).selectedItem.toString()
            val set2_2 = view.findViewById<Spinner>(R.id.reSet2_2).selectedItem.toString()
            val set3_1 = view.findViewById<Spinner>(R.id.reSet3_1).selectedItem.toString()
            val set3_2 = view.findViewById<Spinner>(R.id.reSet3_2).selectedItem.toString()


            // Comprobar que los campos requeridos estén completos
            if (deporte.isEmpty() || fecha.isEmpty() || winlose.isEmpty() || set1_1 == "-" || set1_2 == "-" || set2_1 == "-" || set2_2 == "-" ) {
                Toast.makeText(context, "Por favor, completa todos los campos requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Crear un nuevo objeto con los datos
            val resultado = hashMapOf(
                "deporte" to deporte,
                "fecha" to fecha,
                "winlose" to winlose,
                "usuario" to usuario,
                "set1_1" to set1_1,
                "set1_2" to set1_2,
                "set2_1" to set2_1,
                "set2_2" to set2_2,
                "set3_1" to set3_1,
                "set3_2" to set3_2
            )

            // Obtener una referencia a la base de datos de Firebase
            val db = FirebaseFirestore.getInstance()

            // Agregar un nuevo documento a la colección "resultados"
            db.collection("resultados")
                .add(resultado)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }

        // Aquí debes obtener la lista de jugadores desde Firestore
        val players = listOf<Player>()
        val rvRanking = view.findViewById<RecyclerView>(R.id.recyclerViewRanking)
        rvRanking.adapter = RankingAdapter(players)


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