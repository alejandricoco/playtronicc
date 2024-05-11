package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
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
import com.google.firebase.auth.FirebaseAuth
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
        var usuario = FirebaseAuth.getInstance().currentUser?.displayName

        // si usuario es null, obten de la coleccion users de firebase el campo usuario y asignalo a usuario
        if (usuario == null) {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            if (userEmail != null) {
                db.collection("users").document(userEmail).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            usuario = document.getString("nombre")
                            // Continúa con el código aquí...
                        } else {
                            Toast.makeText(context, "No se pudo obtener el nombre de usuario.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error obteniendo el nombre de usuario.", exception)
                    }
            } else {
                Toast.makeText(context, "Por favor, inicia sesión para hacer una reserva.", Toast.LENGTH_SHORT).show()
            }
        }

        setupPistas()
        setupFechas()
        setupHoras()

        toggleGroupDeporte.addOnButtonCheckedListener{group, checkedId, isChecked ->
            if (isChecked) {
                for (i in 0 until gridLayoutPistas.childCount) {
                    val chip = gridLayoutPistas.getChildAt(i) as Chip
                    chip.isChecked = false
                }
                for (i in 0 until chipGroupFechas.childCount) {
                    val chip = chipGroupFechas.getChildAt(i) as Chip
                    chip.isChecked = false
                }
                for (i in 0 until chipGroupHoras.childCount) {
                    val chip = chipGroupHoras.getChildAt(i) as Chip
                    chip.isChecked = false
                }
                loadReservas()
            }
        }

        buttonReservar.setOnClickListener {
            val deporte = if (toggleGroupDeporte.checkedButtonId == R.id.btnTenis) "tenis" else "padel"
            val pista = getSelectedChipText(gridLayoutPistas)
            val dia = getSelectedChipText(chipGroupFechas)
            val hora = getSelectedChipText(chipGroupHoras)


            if (pista.isEmpty() || dia.isEmpty() || hora.isEmpty()) {
                Toast.makeText(context, "Asegúrate de haber seleccionado deporte, pista," +
                                            " dia y hora disponibles por favor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
        val deporteSeleccionado = if (toggleGroupDeporte.checkedButtonId == R.id.btnTenis) "tenis" else "padel"
        val pistaSeleccionada = getSelectedChipText(gridLayoutPistas)
        val diaSeleccionado = getSelectedChipText(chipGroupFechas)

        db.collection("reservas")
            .whereEqualTo("deporte", deporteSeleccionado)
            .whereEqualTo("pista", pistaSeleccionada)
            .whereEqualTo("dia", diaSeleccionado)
            .get()
            .addOnSuccessListener { result ->
                for (i in 0 until chipGroupHoras.childCount) {
                    val chipHora = chipGroupHoras.getChildAt(i) as Chip
                    val hora = chipHora.text.toString()
                    val horaEstaReservada = result.any { it.getString("hora") == hora }
                    chipHora.isEnabled = !horaEstaReservada
                    if (horaEstaReservada) {
                        chipHora.paintFlags = chipHora.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        chipHora.paintFlags = chipHora.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
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
                        for (i in 0 until chipGroupFechas.childCount) {
                            val chipFecha = chipGroupFechas.getChildAt(i) as Chip
                            chipFecha.isChecked = false
                        }
                        for (i in 0 until chipGroupHoras.childCount) {
                            val chipHora = chipGroupHoras.getChildAt(i) as Chip
                            chipHora.isChecked = false
                        }
                        //setupFechas()
                        //setupHoras()
                        loadReservas()
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
                        for (i in 0 until chipGroupHoras.childCount) {
                            val chipHora = chipGroupHoras.getChildAt(i) as Chip
                            chipHora.isChecked = false
                        }
                        //setupHoras()
                        loadReservas()
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
}