package com.example.playtronic.fragments.fragmentsMenu

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentManager.TAG
import androidx.recyclerview.widget.RecyclerView
import com.example.playtronic.Mensaje
import com.example.playtronic.MensajesAdapter
import com.example.playtronic.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FragmentChat : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerViewMensajes: RecyclerView
    private lateinit var mensajesAdapter: MensajesAdapter
    private var nombreUsuario: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        nombreUsuario = sharedPreferences.getString("nombreUsuario", "Default")



        recyclerViewMensajes = view.findViewById<RecyclerView>(R.id.recyclerViewMensajes)
        // configuramos interfaz de chat aqui
        mensajesAdapter = MensajesAdapter(mutableListOf())
        recyclerViewMensajes.adapter = mensajesAdapter

        // Añadimos un LinearLayoutManager para que los mensajes se muestren en orden
        recyclerViewMensajes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        val editTextMensaje = view.findViewById<EditText>(R.id.editTextMensaje)
        val buttonEnviar = view.findViewById<Button>(R.id.buttonEnviar)

        view.viewTreeObserver.addOnGlobalLayoutListener {
            // Comprueba si el teclado está abierto
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - r.bottom

            // Si el teclado está abierto, desplaza el RecyclerView a la posición del último mensaje
            if (keypadHeight > screenHeight * 0.15) {
                recyclerViewMensajes.post {
                    recyclerViewMensajes.smoothScrollToPosition(mensajesAdapter.itemCount - 1)
                }
            }
        }

        buttonEnviar.setOnClickListener {
            val contenido = editTextMensaje.text.toString()

            if (contenido.isNotBlank()) {
                enviarMensaje(contenido)
                editTextMensaje.text.clear()
            }
        }

        recibirMensajes()

    }


    @SuppressLint("RestrictedApi")
    private fun enviarMensaje(contenido: String) {
        val idRemitente = auth.currentUser?.uid

        if (idRemitente == null) {
            Log.w(TAG, "ID del remitente no disponible")
            return
        }

        // Primero verifica si el nombre del remitente está disponible
        val nombreDisponible = auth.currentUser?.displayName

        if (nombreDisponible != null) {
            // Si el nombre está disponible, procede a enviar el mensaje
            enviarMensajeConNombre(contenido, idRemitente, nombreDisponible)
        } else {
           enviarMensajeConNombre(contenido, idRemitente, nombreUsuario!!)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun enviarMensajeConNombre(contenido: String, idRemitente: String, nombreRemitente: String) {
        val fechaEnvio = System.currentTimeMillis()
        val mensaje = Mensaje(contenido, idRemitente, nombreRemitente, fechaEnvio)

        // Guarda el mensaje en la base de datos
        db.collection("Mensajes").add(mensaje)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Mensaje enviado con ID: ${documentReference.id}")
                recyclerViewMensajes.scrollToPosition(mensajesAdapter.itemCount - 1)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al enviar mensaje", e)
            }
    }


    @SuppressLint("RestrictedApi")
    private fun recibirMensajes() {
        // Consulta la base de datos para obtener todos los mensajes del grupo de chat
        db.collection("Mensajes")
            .orderBy("fechaEnvio")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Error al recibir mensajes", e)
                    return@addSnapshotListener
                }

                // Actualiza la interfaz de usuario con los mensajes recibidos
                val mensajes = snapshots?.toObjects(Mensaje::class.java)
                if (mensajes != null) {
                    mensajesAdapter.mensajes.clear()
                    mensajesAdapter.mensajes.addAll(mensajes)
                    mensajesAdapter.notifyDataSetChanged()
                    recyclerViewMensajes.scrollToPosition(mensajesAdapter.itemCount - 1)
                }
            }
    }

    @SuppressLint("RestrictedApi")
    private fun obtenerNombreUsuario(idRemitente: String?, callback: (String) -> Unit) {
        db.collection("users")
            .whereEqualTo("uid", idRemitente)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val nombreRemitente = document.getString("usuario")
                    callback(nombreRemitente!!)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error al obtener nombre del usuario", exception)
            }
    }


    private fun cargarFragment(fragment: Fragment) {
        // Asegúrate de que la actividad contenedora no sea nula
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.drawer_layout,
            fragment
        ) // cargamos en el drawer del MenuActivity el fragmento que queremos
        fragmentTransaction.addToBackStack(null) // Permite volver al FragmentLogin al presionar el botón atrás
        fragmentTransaction.commit()
    }
}