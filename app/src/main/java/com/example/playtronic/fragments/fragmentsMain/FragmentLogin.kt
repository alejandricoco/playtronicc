package com.example.playtronic.fragments.fragmentsMain

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.playtronic.MenuActivity
import com.example.playtronic.R
import com.example.playtronic.TwitterRepository
import com.facebook.AccessToken
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.LoginManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.RequestToken
import twitter4j.conf.ConfigurationBuilder

class FragmentLogin : Fragment() {

    private lateinit var twitterRepository: TwitterRepository
    private lateinit var twitter: Twitter
    private var requestToken: RequestToken? = null

    //private lateinit var facebookButton: Button
    private lateinit var callbackManager: CallbackManager
    private lateinit var auth: FirebaseAuth
    //private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var googleSignInClient: GoogleSignInClient
    private val sharedPref by lazy { requireActivity().getSharedPreferences("playtronic", Context.MODE_PRIVATE) }
    private lateinit var imailInputt: TextInputEditText
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    private fun cargarFragment(fragment: Fragment) {
        // Asegúrate de que la actividad contenedora no sea nula
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activityMainFrameLayout, fragment)
        fragmentTransaction.addToBackStack(null) // Permite volver al FragmentLogin al presionar el botón atrás
        fragmentTransaction.commit()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        twitterRepository = TwitterRepository(requireContext())

        // Inicializa el CallbackManager
        callbackManager = CallbackManager.Factory.create()
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val btnOlvidaContrasena = view.findViewById<Button>(R.id.btnOlvidaContrasena)
        val button2 = view.findViewById<Button>(R.id.button2)
        val googleButton = view.findViewById<Button>(R.id.buttonGoogle)
        val twitterButton = view.findViewById<Button>(R.id.buttonFacebook)
        //facebookButton = view.findViewById<Button>(R.id.buttonFacebook)
        imailInputt = view.findViewById<TextInputEditText>(R.id.inputImail)
        val passwordInput = view.findViewById<EditText>(R.id.inputPass)
        val loginBoton = view.findViewById<Button>(R.id.buttonLogin)



        imailInputt.addTextChangedListener(createTextWatcher(::validateEmail))

        // Configura Twitter4J
        val builder = ConfigurationBuilder()
        builder.setOAuthConsumerKey(R.string.com_twitter_sdk_android_CONSUMER_KEY.toString())
        builder.setOAuthConsumerSecret(R.string.com_twitter_sdk_android_CONSUMER_SECRET.toString())
        val configuration = builder.build()

        val twitterFactory = TwitterFactory(configuration)
        twitter = twitterFactory.instance

        //val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.default_web_client_id))
            //.requestEmail()
            //.build()
        //googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        btnOlvidaContrasena.setOnClickListener {
            cargarFragment(FragmentOlvidaContrasena())
        }

        button2.setOnClickListener {
            val snack = Snackbar.make(it, "Has pulsado en el boton de Registrar", Snackbar.LENGTH_LONG)
            snack.show()
            cargarFragment(FragmentCreaCuenta())

        }

        googleButton.setOnClickListener {
            iniciarsesionGoogle()
        }



        twitterButton.setOnClickListener {
            loginWithTwitter()
        }



        // GUARDAMOS EN UNA VARIABLE CADA LOGO
        val googleImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireContext().getDrawable(R.drawable.iconogoogle)
        } else {
            TODO()
        }

        // GUARDAMOS EN UNA VARIABLE CADA LOGO
        val facebookImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireContext().getDrawable(R.drawable.tuiter)
        } else {
            TODO()
        }


        // ASI LA IMAGEN ESTARÁ EN LA IZQ DEL BOTÓN, LO DEMÁS A NULL
        googleButton.setCompoundDrawablesWithIntrinsicBounds(googleImage, null, null, null)
        twitterButton.setCompoundDrawablesWithIntrinsicBounds(facebookImage, null, null, null)


        //CUANDO CLIQUEMOS EL BOTON LOGIN COMPROBARÁ QUE USUARIO Y CONTRASEÑA NO QUEDARON VACÍOS

        //inicializamos firebaseAuth

        loginBoton.setOnClickListener {
            val email = imailInputt.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty()) {
                imailInputt.error = "El campo de usuario no puede estar vacío"
            }

            if (password.isEmpty()) {
                passwordInput.error = "El campo de contraseña no puede estar vacío"
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // SI USER Y PASS ESTAN RELLENOS, INICIAMOS SESION Y VAMOS A LA PANTALLA SIGUIENTE

                sharedPref.edit().putString("email", email).apply()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) //si el usuario está logeado en la app y por lo tanto en firebase
                            {
                                val user = auth.currentUser
                                if (user?.isEmailVerified == true) { // Si el correo electrónico ha sido verificado
                                    val intent = Intent(activity, MenuActivity::class.java)
                                    startActivity(intent)

                                } else { // Si el correo electrónico no ha sido verificado
                                    cargarFragment(FragmentAvisoMustVerify())
                                    //Toast.makeText(requireContext(), "Por favor, debes verificar tu correo electrónico antes de iniciar sesión", Toast.LENGTH_SHORT).show()
                                }
                                // El inicio de sesión fue exitoso

                            }
                        else { //Si el usuario no está logeado en la app y por lo tanto tampoco en firebase
                            // haz un toast diciendo que falló el inicio de sesion en este else
                            val dialog = AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
                                .setTitle("Error al iniciar sesión")
                                .setMessage("El correo electrónico o la contraseña son incorrectos.")
                                .setPositiveButton("Aceptar", null)
                                .show()

                            // Cambia el color del texto del mensaje
                            val messageView = dialog.findViewById<TextView>(android.R.id.message)
                            messageView?.setTextColor(Color.WHITE)
                            }
                    }





            }
        }


    }

    private fun loginWithTwitter() {
        val provider = OAuthProvider.newBuilder("twitter.com")

        val activity = requireActivity()
        auth.startActivityForSignInWithProvider(activity, provider.build())
            .addOnSuccessListener { authResult ->
                // El usuario ha iniciado sesión con éxito
                val user = authResult.user
                if (user != null) {
                    val usuario = user.displayName.toString()
                    val photourl = user.photoUrl.toString()

                    // Guardar la información del usuario en Firestore
                    val userDocRef = db.collection("users").document("(Twitter) $usuario")
                    userDocRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            // El documento ya existe, no restablecer el nivel a null
                        } else {
                            // El documento no existe, crearlo
                            userDocRef.set(
                                hashMapOf(
                                    "usuario" to usuario,
                                    "email" to null, // No podemos obtener el email del usuario twitter
                                    "deporte" to null, // No podemos obtener el deporte favorito del usuario twitter
                                    "nivel" to null, // No podemos obtener el nivel del usuario twitter
                                    "fecha_nac" to null,// No podemos obtener la fecha de nacimiento del usuario twitter
                                    "photourl" to photourl,
                                    "plataforma" to "Twitter",
                                )
                            )
                        }
                    }
                }
                llamarMenu()

            }
            .addOnFailureListener { e ->
                // El inicio de sesión ha fallado, maneja el error
                Toast.makeText(context, "Error al iniciar sesión con Twitter: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Maneja el callback de la redirección de Twitter
        val uri = activity?.intent?.data
        if (uri != null && uri.toString().startsWith("YOUR_CALLBACK_URL")) {
            val verifier = uri.getQueryParameter("oauth_verifier") ?: return
            Thread {
                try {
                    val accessToken = twitter.getOAuthAccessToken(requestToken, verifier)
                    // Aquí puedes guardar el accessToken para futuras sesiones
                    // y proceder con el usuario logueado en tu aplicación
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun iniciarsesionGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            manageresult(task)
        }

    }

    private fun manageresult(task: Task<GoogleSignInAccount>) {
        val account : GoogleSignInAccount? = task.getResult(ApiException::class.java)

        if(account != null){
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // El usuario ha iniciado sesión con éxito
                        val user = auth.currentUser
                        if (user != null) {
                            val nombre = user.displayName ?: "Nombre desconocido"
                            val email = user.email ?: "Email desconocido"
                            val photourl = user.photoUrl?.toString() ?: "URL de foto desconocida"

                            // Guardar la información del usuario en Firestore
                            val userDocRef = db.collection("users").document("(Twitter) $nombre")
                            userDocRef.get().addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // El documento ya existe así que no restablecemos el nivel a null
                                } else {
                                    // El documento no existe, lo creamos
                                    userDocRef.set(
                                        hashMapOf(
                                            "usuario" to nombre,
                                            "email" to email, // No podemos obtener el email del usuario twitter
                                            "deporte" to null, // No podemos obtener el deporte favorito del usuario twitter
                                            "nivel" to null, // No podemos obtener el nivel del usuario twitter
                                            "fecha_nac" to null,// No podemos obtener la fecha de nacimiento del usuario twitter
                                            "photourl" to photourl,
                                            "plataforma" to "Google",
                                        )
                                    )
                                }
                            }
                        }
                        llamarMenu()
                        Toast.makeText(requireActivity(), "Iniciada la sesión con Google", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireActivity(), "Error al iniciar sesion con Google", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun loginWithFacebook() {
        val loginManager = LoginManager.getInstance()
        loginManager.logIn(requireActivity(), listOf("email"))

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                // El inicio de sesión fue exitoso, puedes usar loginResult para acceder al token de acceso
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                // El usuario canceló el inicio de sesión
            }

            override fun onError(error: FacebookException) {
                // Ocurrió un error durante el inicio de sesión
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    llamarMenu()
                    Toast.makeText(requireActivity(), "Iniciada la sesión con Facebook", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireActivity(), "Error al iniciar sesion con Facebook", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun llamarMenu(){
        val intent = Intent(activity, MenuActivity::class.java)
        startActivity(intent)
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

    private fun validateEmail(email: String) {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        if (!emailPattern.matcher(email).matches()) {
            imailInputt.error = "Introduce una dirección de correo electrónico válida"
        } else {
            imailInputt.error = null
        }
    }


    /*


    private fun llamarMenu(){
        val intent = Intent(activity, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun iniciarsesionGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            manageresult(task)
        }

    }

    private fun manageresult(task: Task<GoogleSignInAccount>) {
        val account : GoogleSignInAccount? = task.getResult(ApiException::class.java)

        if(account != null){
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        llamarMenu()
                        Toast.makeText(requireActivity(), "Inicio de sesion correcto", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireActivity(), "Error al iniciar sesion", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    //Funcion para comprobar el inicio de sesion
    private fun comprobarInicioSesion(email: String, password: String)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    //Mostramos un mensaje y llamamos al menu
                    Toast.makeText(requireActivity(), "Inicio de sesion correcto", Toast.LENGTH_SHORT).show()
                    llamarMenu()
                } else {
                    //Si no se ha podido iniciar sesion mostramos un mensaje
                    Toast.makeText(requireActivity(), "Error al iniciar sesion, comprueba los datos", Toast.LENGTH_SHORT).show()
                }
            }
    }


    */


    companion object {
        @JvmStatic
        fun newInstance() = FragmentLogin()
    }
}