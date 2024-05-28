package com.example.playtronic.fragments.fragmentsMenu

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.playtronic.Player
import com.example.playtronic.R
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Crear un ArrayAdapter con los números del 1 al 7
        val numbers = arrayOf("-", "1", "2", "3", "4", "5", "6", "7")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, numbers)
        // Aplicar el adapter al spinner
        val spinnerIds = arrayOf(R.id.reSet1_1, R.id.reSet1_2, R.id.reSet2_1, R.id.reSet2_2, R.id.reSet3_1, R.id.reSet3_2)
        for (id in spinnerIds) {
            val spinner: Spinner = view.findViewById(id)
            spinner.adapter = adapter
        }

        val otherViews = arrayOf(R.id.btnSubirResultado, R.id.btnHecho, R.id.tilTenisPadel, R.id.fecha, R.id.tilVictoriaDerrota, R.id.set1, R.id.set2, R.id.set3) + spinnerIds
        for (id in otherViews) {
            view.findViewById<View>(id).visibility = View.GONE
        }

        // Configurar el toggle group
        val toggleGroup: MaterialButtonToggleGroup = view.findViewById(R.id.toggleGroupRankingResultados)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewRanking)
        toggleGroup.check(R.id.btnRanking)
        recyclerView.visibility = View.VISIBLE
        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) { // Solo cambiar la visibilidad cuando un botón se selecciona
                if (checkedId == R.id.btnRanking) {
                    recyclerView.visibility = View.VISIBLE
                    for (id in otherViews) {
                        view.findViewById<View>(id).visibility = View.GONE
                    }
                } else if (checkedId == R.id.btnResultados) {
                    recyclerView.visibility = View.GONE
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


        val btnSubirResultado = view.findViewById<Button>(R.id.btnSubirResultado)
        btnSubirResultado.setOnClickListener {

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