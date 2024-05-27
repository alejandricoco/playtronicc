package com.example.playtronic

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PartidoEliminarAdapter(private val partidos: MutableList<Partido>) : RecyclerView.Adapter<PartidoEliminarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidoEliminarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.partido_eliminar_item, parent, false)
        return PartidoEliminarViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartidoEliminarViewHolder, position: Int) {
        val partido = partidos[position]

        // Aquí puedes rellenar los datos de la vista con los datos del partido
        holder.view.findViewById<TextView>(R.id.textViewCreadoPor).text = "Creado por: ${partido.creadoPor}"
        holder.view.findViewById<TextView>(R.id.tvDeporte).text = "Partido de ${partido.deporte}"
        holder.view.findViewById<TextView>(R.id.tvHorario).text = "Horario: ${partido.horarioPreferido}"
        holder.view.findViewById<TextView>(R.id.tvNivelOponente).text = partido.nivelOponente

        // Actualiza el TextView con la lista de usuarios que se han unido al partido
        var tvUsuariosUnidos = holder.view.findViewById<TextView>(R.id.tvUsuariosUnidos)
        var usuariosUnidos = partido.usuariosUnidos.joinToString(", ") { usuarioId ->
            if (usuarioId == FirebaseAuth.getInstance().currentUser?.uid) "Yo" else usuarioId
        }
        tvUsuariosUnidos.text = "Usuarios unidos: $usuariosUnidos"

        val btnDelete = holder.view.findViewById<Button>(R.id.btnDelete)
        val btnLeave = holder.view.findViewById<Button>(R.id.btnLeave)

        val user = FirebaseAuth.getInstance().currentUser
        val sharedPreferences = holder.view.context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        //Si el usuario firebase es nulo, se obtiene el nombre de usuario guardado en SharedPreferences
        val username = user?.displayName ?: sharedPreferences.getString("nombreUsuario", "Default")

        if (partido.creadoPor == username) {
            // El usuario actual es el creador del partido, muestra el botón de eliminar
            holder.view.findViewById<TextView>(R.id.textViewCreadoPor).text = "Creado por mí"
            btnDelete.visibility = View.VISIBLE
            btnLeave.visibility = View.GONE
        } else {
            // El usuario actual no es el creador del partido, muestra el botón de abandonar
            btnDelete.visibility = View.GONE
            btnLeave.visibility = View.VISIBLE
        }

        holder.view.findViewById<TextView>(R.id.tvDeporte).text = "Partido de ${partido.deporte}"
        holder.view.findViewById<TextView>(R.id.tvHorario).text = "Horario: ${partido.horarioPreferido}"
        holder.view.findViewById<TextView>(R.id.tvNivelOponente).text = partido.nivelOponente

        // Actualiza el TextView con la lista de usuarios que se han unido al partido
        tvUsuariosUnidos = holder.view.findViewById<TextView>(R.id.tvUsuariosUnidos)
        var usuariosUnidosList = partido.usuariosUnidos.map { usuarioId ->
            if (usuarioId == username) "Yo" else usuarioId
        }
        usuariosUnidos = when {
            usuariosUnidosList.isEmpty() -> ""
            usuariosUnidosList.size == 1 -> usuariosUnidosList[0]
            else -> usuariosUnidosList.dropLast(1).joinToString(", ") + " y " + usuariosUnidosList.last()
        }
        tvUsuariosUnidos.text = "Usuarios unidos: $usuariosUnidos"

        btnDelete.setOnClickListener {
            // Obtén la referencia al partido
            val db = FirebaseFirestore.getInstance()
            val partidoRef = db.collection("partidos").document(partido.id)

            // Muestra un diálogo de confirmación cuando se hace clic en el botón
            AlertDialog.Builder(holder.view.context)
                .setTitle("Confirmación")
                .setMessage("¿Quieres eliminar el partido?")
                .setPositiveButton("Sí") { dialog, which ->
                    // Elimina el partido de Firebase
                    partidoRef.delete().addOnSuccessListener {
                        Toast.makeText(holder.view.context, "Partido eliminado con éxito", Toast.LENGTH_SHORT).show()
                        // Actualiza la interfaz de usuario
                        partidos.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, partidos.size)
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        btnLeave.setOnClickListener {
            // Código para abandonar el partido...
            val db = FirebaseFirestore.getInstance()
            val partidoRef = db.collection("partidos").document(partido.id)

            // Muestra un diálogo de confirmación cuando se hace clic en el botón
            AlertDialog.Builder(holder.view.context)
                .setTitle("Confirmación")
                .setMessage("¿Quieres abandonar el partido?")
                .setPositiveButton("Sí") { dialog, which ->
                    partidoRef.update("usuariosUnidos", FieldValue.arrayRemove(username)).addOnSuccessListener {
                        // Decrementa el contador del partido
                        partidoRef.update("contador", FieldValue.increment(-1)).addOnSuccessListener {
                            Toast.makeText(holder.view.context, "Has abandonado el partido", Toast.LENGTH_SHORT).show()
                            // Actualiza la interfaz de usuario
                            partidos.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, partidos.size)
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

    }

    override fun getItemCount() = partidos.size
}