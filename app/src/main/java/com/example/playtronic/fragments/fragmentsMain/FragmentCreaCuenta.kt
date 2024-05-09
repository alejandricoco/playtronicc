package com.example.playtronic.fragments.fragmentsMain

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.TAG
import com.example.playtronic.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class FragmentCreaCuenta : Fragment() {

    private lateinit var inputUser: TextInputEditText
    private lateinit var inputNombree: TextInputEditText
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputFechaNac: TextInputEditText
    private lateinit var inputPass1: TextInputEditText
    private lateinit var inputPass2: TextInputEditText
    private val db = FirebaseFirestore.getInstance()

    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()

        return inflater.inflate(R.layout.fragment_crea_cuenta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        inputUser = view.findViewById(R.id.inputUser1)
        inputEmail = view.findViewById(R.id.inputEmail)
        inputNombree = view.findViewById<TextInputLayout>(R.id.nombreCompleto).editText as TextInputEditText
        inputFechaNac = view.findViewById(R.id.inputFechaNac)
        inputPass1 = view.findViewById(R.id.inputPass1)
        inputPass2 = view.findViewById(R.id.inputPass2)

        inputUser.addTextChangedListener(createTextWatcher(::validateUser))
        inputEmail.addTextChangedListener(createTextWatcher(::validateEmail))
        inputPass1.addTextChangedListener(createTextWatcher(::validatePassword))
        inputPass2.addTextChangedListener(createTextWatcher(::validatePassword))


        inputFechaNac.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    inputFechaNac.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        val btnCrearCuenta = view.findViewById<Button>(R.id.buttonCrearCuenta)
        btnCrearCuenta.setOnClickListener {
            val email = inputEmail.text.toString()
            val password = inputPass1.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // La creación de la cuenta fue exitosa
                            Toast.makeText(requireContext(), "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show()
                            val userr = auth.currentUser
                            if (userr != null) {

                                // Guardamos el nombre de usuario en SharedPreferences
                                val editor = sharedPreferences.edit()
                                editor.putString("nombreUsuario", inputNombree.text.toString())
                                editor.apply()

                                db.collection("users").document(email).set(
                                    hashMapOf(
                                        "usuario" to inputUser.text.toString(),
                                        "nombre" to inputNombree.text.toString(),
                                        "email" to inputEmail.text.toString(),
                                        "fecha_nac" to inputFechaNac.text.toString(),
                                        "photourl" to null, // para correo, no hay foto de perfil
                                        "nivel" to null,
                                        "deporte" to null
                                    )
                                )

                            }





                            // Envia un correo electrónico de verificación
                            val user = auth.currentUser
                            user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    Toast.makeText(requireContext(), "Correo de verificación enviado", Toast.LENGTH_SHORT).show()
                                    cargarFragment(FragmentAvisoEnlaceAcc())
                                } else {
                                    Toast.makeText(requireContext(), "Error al enviar el correo de verificación", Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            // Si la creación de la cuenta falla, muestra un mensaje al usuario
                            Toast.makeText(requireContext(), "Error al crear la cuenta", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun cargarFragment(fragment: Fragment) {
        // Asegúrate de que la actividad contenedora no sea nula
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activityMainFrameLayout, fragment) // cargamos en el FrameLayout del activity_login el fragmento que queremos
        fragmentTransaction.addToBackStack(null) // Permite volver al FragmentLogin al presionar el botón atrás
        fragmentTransaction.commit()
    }

    private fun createTextWatcher(validationFunction: (String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                validationFunction(s.toString())
            }
        }
    }

    private fun validateUser(user: String) {
        if (user.length < 5 || !user.any { it.isDigit() }) {
            inputUser.error = "El nombre de usuario debe tener al menos 5 caracteres y al menos un número"
        } else {
            inputUser.error = null
        }
    }

    private fun validateEmail(email: String) {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        if (!emailPattern.matcher(email).matches()) {
            inputEmail.error = "Introduce una dirección de correo electrónico válida"
        } else {
            inputEmail.error = null
        }
    }

    private fun validatePassword(password: String) {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$".toRegex()
        if (!passwordPattern.matches(password)) {
            inputPass1.error = "La contraseña debe tener al menos 6 caracteres, una mayúscula, minúsculas y un número"
        } else {
            inputPass1.error = null
        }

        if (inputPass1.text.toString() != inputPass2.text.toString()) {
            inputPass2.error = "Las contraseñas no coinciden"
        } else {
            inputPass2.error = null
        }
    }

    companion object {
        private const val TAG = "FragmentCreaCuenta"
    }
}