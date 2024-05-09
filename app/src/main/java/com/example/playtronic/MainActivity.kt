package com.example.playtronic

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.playtronic.fragments.fragmentsMain.FragmentLogin
import com.example.playtronic.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding   //DECLARAMOS VARIABLE DE VINCULACION
    private val sharedPref by lazy { getSharedPreferences("playtronic", MODE_PRIVATE) }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        // Configura Twitter4J con tus claves de API de Twitter
        val cb = ConfigurationBuilder()
        cb.setDebugEnabled(true)
            .setOAuthConsumerKey(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY))
            .setOAuthConsumerSecret(getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET))
            .setOAuthAccessToken(getString(R.string.com_twitter_sdk_android_ACCESS_TOKEN))
            .setOAuthAccessTokenSecret(getString(R.string.com_twitter_sdk_android_ACCESS_TOKEN_SECRET))

        val tf = TwitterFactory(cb.build())
        val twitter = tf.instance

        if (savedInstanceState == null) {
            cargarFragment(FragmentLogin())
        }
        // Comprueba si el usuario ya está conectado
        /*
        val username = sharedPref.getString("user", "")
        if (username != null && username.isNotEmpty()) {
            // Si el usuario ya está conectado, redirige a MenuActivity
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish() // Finaliza MainActivity para que el usuario no pueda volver a ella al presionar el botón de retroceso
        } else {
            if (savedInstanceState == null) {
                cargarFragment(FragmentLogin())
            }  //INFLAMOS VISTA UTILIZANDO CLASE DE VINCULACION

            setContentView(binding.root)  //ESTABLECEMOS LA VISTA DEL ACTIVITY
        }

            */


    }


    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.activityMainFrameLayout)

        if (currentFragment is FragmentLogin) {
            // Si el fragmento actual es FragmentLogin, termina la actividad
            super.onBackPressed()
        } else {
            // Si el fragmento actual no es FragmentLogin, carga el FragmentLogin
            cargarFragment(FragmentLogin())
        }
    }



    private fun cargarFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activityMainFrameLayout, fragment)
        fragmentTransaction.commit()
    }


}
