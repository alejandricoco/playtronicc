package com.example.playtronic

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class PartidoAdapter(private val partidos: List<Partido>) : RecyclerView.Adapter<PartidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.partido_item, parent, false)
        return PartidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartidoViewHolder, position: Int) {
        val partido = partidos[position]

        // Aquí puedes rellenar los datos de la vista con los datos del partido
        holder.view.findViewById<TextView>(R.id.textViewCreadoPor).text = "Creado por: ${partido.creadoPor}"
        holder.view.findViewById<TextView>(R.id.tvDeporte).text = "Partido de ${partido.deporte}"
        holder.view.findViewById<TextView>(R.id.tvHorario).text = "Horario: ${partido.horarioPreferido}"
        holder.view.findViewById<TextView>(R.id.tvNivelOponente).text = partido.nivelOponente

        val btnJoin = holder.view.findViewById<Button>(R.id.btnJoin)
        btnJoin.setOnClickListener {
            // Muestra un diálogo de confirmación cuando se hace clic en el botón
            AlertDialog.Builder(holder.view.context)
                .setTitle("Confirmación")
                .setMessage("¿Quieres unirte al partido?")
                .setPositiveButton("Sí") { dialog, which ->
                    // Si el usuario confirma, verifica si ya se ha unido al partido
                    val db = FirebaseFirestore.getInstance()
                    val partidoRef = db.collection("partidos").document(partido.id)
                    val usuarioId = FirebaseAuth.getInstance().currentUser?.uid // Get the user's ID
                    if (usuarioId != null) {
                        partidoRef.get().addOnSuccessListener { snapshot ->
                            val partidoActualizado = snapshot.toObject(Partido::class.java)
                            if (partidoActualizado != null) {
                                if (partidoActualizado.usuariosUnidos.contains(usuarioId)) {
                                    // El usuario ya se ha unido al partido, muestra un mensaje
                                    Toast.makeText(holder.view.context, "Ya te has unido a este partido", Toast.LENGTH_SHORT).show()
                                } else {
                                    // El usuario no se ha unido al partido, actualiza el contador y la lista de usuarios unidos
                                    partidoRef.update("contador", FieldValue.increment(1))
                                    partidoRef.update("usuariosUnidos", FieldValue.arrayUnion(usuarioId)).addOnSuccessListener {
                                        // Obtén el partido actualizado de Firebase
                                        partidoRef.get().addOnSuccessListener { snapshot ->
                                            val partidoActualizado = snapshot.toObject(Partido::class.java)
                                            if (partidoActualizado != null) {
                                                // Actualiza la interfaz de usuario para reflejar el cambio
                                                updateUI(partidoActualizado, btnJoin)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Actualiza la interfaz de usuario en función del contador
        updateUI(partido, btnJoin)

    }


private fun updateUI(partido: Partido, btnJoin: Button) {
    if (partido.deporte == "Padel" && partido.contador >= 3 || partido.deporte == "Tenis" && partido.contador >= 1) {
        // Si el partido está lleno, oculta el botón de unirse
        btnJoin.visibility = View.GONE
    } else {
        // Si el partido no está lleno, muestra el botón de unirse y actualiza el texto para reflejar el número de espacios disponibles
        btnJoin.visibility = View.VISIBLE
        val espaciosDisponibles = if (partido.deporte == "Padel") 3 - partido.contador else 1 - partido.contador
        btnJoin.text = "+$espaciosDisponibles"
    }
}
    override fun getItemCount() = partidos.size
}