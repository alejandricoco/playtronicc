package com.example.playtronic.fragments.fragmentsMain

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.playtronic.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentOlvidaContrasena.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentOlvidaContrasena : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_olvida_contrasena, container, false)
    }


    private fun cargarFragment(fragment: Fragment) {
        // Asegúrate de que la actividad contenedora no sea nula
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activityMainFrameLayout, fragment)
        fragmentTransaction.addToBackStack(null) // Permite volver al FragmentLogin al presionar el botón atrás
        fragmentTransaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        val inputEmail = view.findViewById<EditText>(R.id.inputEmail)
        val textinputlayoutemail = view.findViewById<TextInputLayout>(R.id.textinputlayoutemail)
        val btnRestContrasena = view.findViewById<Button>(R.id.buttonRestContrasena)


        // Agrega el TextWatcher al EditText del correo electrónico
        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // No es necesario implementar este método
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // No es necesario implementar este método
            }

            override fun afterTextChanged(s: Editable) {
                // Valida el correo electrónico después de que el texto haya cambiado
                validateEmail(s.toString(), textinputlayoutemail)
            }
        })


        btnRestContrasena.setOnClickListener {
            val email = inputEmail.text.toString()
            if (email.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val documents = task.result
                            if (!documents.isEmpty) {
                                // El correo electrónico existe en la colección de usuarios
                                auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // El correo electrónico se envió correctamente
                                            cargarFragment(FragmentAvisoRestPass())
                                        } else {
                                            // Hubo un error al enviar el correo electrónico
                                            AlertDialog.Builder(requireContext())
                                                .setTitle("Error")
                                                .setMessage("Hubo un error al enviar el correo electrónico de restablecimiento de contraseña. Por favor, inténtalo de nuevo.")
                                                .setPositiveButton("Aceptar", null)
                                                .show()
                                        }
                                    }
                            } else {
                                // El correo electrónico no existe en la colección de usuarios
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Email no válido")
                                    .setMessage("El email introducido no está registrado en la aplicación. Por favor, introduce un email válido.")
                                    .setPositiveButton("Aceptar", null)
                                    .show()
                            }
                        } else {
                            // Hubo un error al realizar la consulta
                            AlertDialog.Builder(requireContext())
                                .setTitle("Error")
                                .setMessage("Hubo un error al verificar el correo electrónico. Por favor, inténtalo de nuevo.")
                                .setPositiveButton("Aceptar", null)
                                .show()
                        }
                    }
            } else {
                inputEmail.error = "Introduce tu dirección de correo electrónico"
            }
        }




    }



    private fun validateEmail(email: String, textInputLayout: TextInputLayout) {
        val emailPattern = Patterns.EMAIL_ADDRESS
        if (emailPattern.matcher(email).matches()) {
            // La dirección de correo electrónico es válida
            textInputLayout.error = null
        } else {
            // La dirección de correo electrónico no es válida
            textInputLayout.error = "Introduce una dirección de correo electrónico válida"
        }
    }


}