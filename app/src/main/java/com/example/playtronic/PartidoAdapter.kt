package com.example.playtronic

import android.app.AlertDialog
import android.content.Context
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
import java.math.BigDecimal

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
            // Obtain the username and the reference to the game
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email
            val sharedPreferences = holder.view.context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
            val username = user?.displayName ?: sharedPreferences.getString("nombreUsuario", "Default")

            if (username != null && username.isNotEmpty()) {
                if (userEmail != null && userEmail.isNotEmpty()) {
                    val googleUserRef = db.collection("users").document("(Google) $userEmail")
                    val emailUserRef = db.collection("users").document(userEmail)

                    googleUserRef.get().addOnSuccessListener { googleDoc ->
                        if (googleDoc.exists()) {
                            val userNivel = googleDoc.getDouble("nivel") ?: 1.0
                            handleUserJoining(userNivel, partido, holder, username, btnJoin)
                        } else {
                            emailUserRef.get().addOnSuccessListener { emailDoc ->
                                if (emailDoc.exists()) {
                                    val userNivel = emailDoc.getDouble("nivel") ?: 1.0
                                    handleUserJoining(userNivel, partido, holder, username, btnJoin)
                                }
                            }
                        }
                    }
                }

                val twitterUserRef = db.collection("users").document("(Twitter) $username")
                twitterUserRef.get().addOnSuccessListener { twitterDoc ->
                    if (twitterDoc.exists()) {
                        val userNivel = twitterDoc.getDouble("nivel") ?: 1.0
                        handleUserJoining(userNivel, partido, holder, username, btnJoin)
                    }
                }
            }
        }

        // Actualiza la interfaz de usuario en función del contador
        updateUI(partido, btnJoin)
    }

    private fun handleUserJoining(userNivel: Double, partido: Partido, holder: PartidoViewHolder, username: String?, btnJoin: Button) {
        if (isUserNivelValid(userNivel, partido.nivelOponente)) {
            val partidoRef = FirebaseFirestore.getInstance().collection("partidos").document(partido.id)
            partidoRef.get().addOnSuccessListener { snapshot ->
                val partidoActualizado = snapshot.toObject(Partido::class.java)
                if (partidoActualizado != null) {
                    if (partidoActualizado.usuariosUnidos.contains(username)) {
                        Toast.makeText(holder.view.context, "Ya te has unido a este partido", Toast.LENGTH_SHORT).show()
                    } else {
                        AlertDialog.Builder(holder.view.context)
                            .setTitle("Confirmación")
                            .setMessage("¿Quieres unirte al partido?")
                            .setPositiveButton("Sí") { dialog, which ->
                                partidoRef.update("contador", FieldValue.increment(1))
                                partidoRef.update("usuariosUnidos", FieldValue.arrayUnion(username)).addOnSuccessListener {
                                    if (username != null) {
                                        val updatedPartido = Partido(
                                            id = partido.id,
                                            creadoPor = partido.creadoPor,
                                            deporte = partido.deporte,
                                            horarioPreferido = partido.horarioPreferido,
                                            nivelOponente = partido.nivelOponente,
                                            contador = partido.contador + 1,
                                            usuariosUnidos = partido.usuariosUnidos + username
                                        )
                                        updateUI(updatedPartido, btnJoin)
                                    }
                                }
                            }
                            .setNegativeButton("No", null)
                            .show()
                    }
                }
            }
        } else {
            Toast.makeText(holder.view.context, "No puedes unirte a este partido debido a tu nivel", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(partido: Partido, btnJoin: Button) {
        if (partido.id.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val partidoRef = db.collection("partidos").document(partido.id)
            partidoRef.get().addOnSuccessListener { snapshot ->
                val partidoActualizado = snapshot.toObject(Partido::class.java)
                if (partidoActualizado != null) {
                    if (partidoActualizado.deporte == "Padel" && partidoActualizado.contador >= 3 || partidoActualizado.deporte == "Tenis" && partidoActualizado.contador >= 1) {
                        btnJoin.visibility = View.GONE
                    } else {
                        btnJoin.visibility = View.VISIBLE
                        val espaciosDisponibles = if (partidoActualizado.deporte == "Padel") 3 - partidoActualizado.contador else 1 - partidoActualizado.contador
                        btnJoin.text = "+$espaciosDisponibles"
                    }
                }
            }
        }
    }

    private fun isUserNivelValid(userNivel: Double, partidoNivel: String): Boolean {
        val userNivelBD = BigDecimal(userNivel)
        val range = when (partidoNivel) {
            "Casi malo (1 - 3.4)" -> BigDecimal(1.0)..BigDecimal(3.4)
            "Casi bueno (3.5 - 7.0)" -> BigDecimal(3.5)..BigDecimal(7.0)
            "Leyenda (7.1 - 10.0)" -> BigDecimal(7.1)..BigDecimal(10.0)
            "Cualquier nivel" -> BigDecimal(1.0)..BigDecimal(10.0)
            else -> return false
        }
        return userNivelBD in range
    }

    override fun getItemCount() = partidos.size
}

