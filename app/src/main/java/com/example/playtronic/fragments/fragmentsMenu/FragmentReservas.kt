package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.TAG
import com.example.playtronic.MainActivity
import com.example.playtronic.MenuActivity
import com.example.playtronic.R
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FragmentReservas : Fragment() {

    private lateinit var gridLayoutPistas: GridLayout
    private lateinit var chipGroupFechas: GridLayout
    private lateinit var chipGroupHoras: GridLayout
    private lateinit var buttonReservar: Button
    private lateinit var toggleGroupDeporte: MaterialButtonToggleGroup

    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservas, container, false)
        gridLayoutPistas = view.findViewById(R.id.gridLayoutPistas)
        chipGroupFechas = view.findViewById(R.id.chipGroupFechas)
        chipGroupHoras = view.findViewById(R.id.chipGroupHoras)
        buttonReservar = view.findViewById(R.id.button_reservar)
        toggleGroupDeporte = view.findViewById(R.id.toggleGroupDeporte)

        setupPistas()
        setupFechas()
        setupHoras()

        buttonReservar.setOnClickListener {
            val deporte = if (toggleGroupDeporte.checkedButtonId == R.id.btnTenis) "tenis" else "padel"
            val pista = getSelectedChipText(gridLayoutPistas)
            val dia = getSelectedChipText(chipGroupFechas)
            val hora = getSelectedChipText(chipGroupHoras)
            val usuario = "usuario logueado" // Reemplaza esto con el usuario logueado

            val reserva = hashMapOf(
                "deporte" to deporte,
                "pista" to pista,
                "dia" to dia,
                "hora" to hora,
                "usuario" to usuario
            )

            db.collection("reservas")
                .add(reserva)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            Toast.makeText(context, "Reserva realizada con éxito", Toast.LENGTH_SHORT).show()
            // ir al menuactivity
            // Reinicia la MenuActivity
            val intent = Intent(activity, MenuActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadReservas()

    }

    @SuppressLint("RestrictedApi")
    private fun loadReservas() {
        db.collection("reservas")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val deporteReserva = document.getString("deporte") ?: ""
                    val pistaReserva = document.getString("pista") ?: ""
                    val diaReserva = document.getString("dia") ?: ""
                    val horaReserva = document.getString("hora") ?: ""

                    // Verifica si la reserva corresponde al deporte seleccionado
                    val deporteSeleccionado = if (toggleGroupDeporte.checkedButtonId == R.id.btnTenis) "tenis" else "padel"
                    if (deporteReserva == deporteSeleccionado) {
                        // Encuentra el chip correspondiente a la pista y al día reservados
                        for (i in 0 until gridLayoutPistas.childCount) {
                            val chipPista = gridLayoutPistas.getChildAt(i) as Chip
                            if (chipPista.text == pistaReserva) {
                                for (j in 0 until chipGroupFechas.childCount) {
                                    val chipDia = chipGroupFechas.getChildAt(j) as Chip
                                    if (chipDia.text == diaReserva) {
                                        // Encuentra el chip correspondiente a la hora reservada y deshabilítalo
                                        for (k in 0 until chipGroupHoras.childCount) {
                                            val chipHora = chipGroupHoras.getChildAt(k) as Chip
                                            if (chipHora.text == horaReserva) {
                                                chipHora.isEnabled = false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun getSelectedChipText(gridLayout: GridLayout): String {
        for (i in 0 until gridLayout.childCount) {
            val chip = gridLayout.getChildAt(i) as Chip
            if (chip.isChecked) {
                return chip.text.toString()
            }
        }
        return ""
    }

    private fun disableChip(gridLayout: GridLayout, text: String) {
        for (i in 0 until gridLayout.childCount) {
            val chip = gridLayout.getChildAt(i) as Chip
            if (chip.text == text) {
                chip.isEnabled = false
            }
        }
    }


    private fun setupPistas() {
        val pistas = listOf("Pista 1", "Pista 2", "Pista 3", "Pista 4", "Pista 5", "Pista 6")
        for (pista in pistas) {
            val chip = Chip(context).apply {
                text = pista
                isCheckable = true
                isCheckedIconVisible = false
                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_background_color)
                setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_text_color))
                gravity = Gravity.CENTER
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(40, 0, 40, 0)
                setOnCheckedChangeListener { checkedChip, isChecked ->
                    if (isChecked) {
                        for (i in 0 until gridLayoutPistas.childCount) {
                            val otherChip = gridLayoutPistas.getChildAt(i) as Chip
                            if (otherChip != checkedChip) {
                                otherChip.isChecked = false
                            }
                        }
                    }
                }
            }
            val layoutParams = GridLayout.LayoutParams()
            layoutParams.setMargins(10, 0, 10, 0) // Set margins in pixels. Adjust as needed.
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            chip.layoutParams = layoutParams
            gridLayoutPistas.addView(chip)
        }
    }

    private fun setupFechas() {
        val calendar = Calendar.getInstance()
        chipGroupFechas.columnCount = 4
        chipGroupFechas.rowCount = 2
        for (i in 0 until 8) {
            val date = calendar.time
            val chip = Chip(context).apply {
                text = SimpleDateFormat("EEE dd", Locale.getDefault()).format(date)
                isCheckable = true
                isCheckedIconVisible = false
                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_background_color)
                setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_text_color))
                gravity = Gravity.CENTER
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(20, 0, 20, 0)
                setOnCheckedChangeListener { checkedChip, isChecked ->
                    if (isChecked) {
                        for (i in 0 until chipGroupFechas.childCount) {
                            val otherChip = chipGroupFechas.getChildAt(i) as Chip
                            if (otherChip != checkedChip) {
                                otherChip.isChecked = false
                            }
                        }
                    }
                }
            }
            val layoutParams = GridLayout.LayoutParams()
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT // Set height to wrap content
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT // Set width to wrap content
            layoutParams.setMargins(10, 0, 10, 0) // Set margins in pixels. Adjust as needed.
            layoutParams.columnSpec = GridLayout.spec(i % 4, 1f)
            layoutParams.rowSpec = GridLayout.spec(i / 4, 1f)
            chip.layoutParams = layoutParams
            chipGroupFechas.addView(chip)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    private fun setupHoras() {
        chipGroupHoras.columnCount = 5
        chipGroupHoras.rowCount = 3
        val horas = (8..22).map { "$it:00" }
        for (i in horas.indices) {
            val chip = Chip(context).apply {
                text = horas[i]
                isCheckable = true
                isCheckedIconVisible = false
                chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.chip_background_color)
                setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_text_color))
                gravity = Gravity.CENTER
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(40, 0, 40, 0)
                setOnCheckedChangeListener { checkedChip, isChecked ->
                    if (isChecked) {
                        for (i in 0 until chipGroupHoras.childCount) {
                            val otherChip = chipGroupHoras.getChildAt(i) as Chip
                            if (otherChip != checkedChip) {
                                otherChip.isChecked = false
                            }
                        }
                    }
                }
            }
            val layoutParams = GridLayout.LayoutParams()
            layoutParams.setMargins(10, 0, 10, 0) // Set margins in pixels. Adjust as needed.
            layoutParams.columnSpec = GridLayout.spec(i % 5, 1f)
            layoutParams.rowSpec = GridLayout.spec(i / 5, 1f)
            chip.layoutParams = layoutParams
            chipGroupHoras.addView(chip)
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