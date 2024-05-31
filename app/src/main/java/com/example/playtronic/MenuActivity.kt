package com.example.playtronic

import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.playtronic.databinding.ActivityMenuBinding
import com.example.playtronic.databinding.FragmentJugarBinding
import com.example.playtronic.fragments.fragmentsMenu.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var fragmentManager: FragmentManager
    lateinit var binding: ActivityMenuBinding   //DECLARAMOS VARIABLE DE VINCULACION
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    cargarFragment(FragmentInicio())
                    binding.backButton.visibility = View.GONE
                }
                R.id.bottom_play -> {
                    cargarFragment(FragmentJugar())
                    binding.backButton.visibility = View.GONE
                }
                R.id.bottom_chat -> {
                    cargarFragment(FragmentChat())
                    binding.backButton.visibility = View.GONE
                }
                R.id.bottom_profile -> {
                    cargarFragment(FragmentPerfil())
                    binding.backButton.visibility = View.GONE
                }
            }
            true
        }

        // Configura un OnClickListener para el botón de retroceso personalizado
        binding.backButton.setOnClickListener {
            cargarFragment(FragmentInicio())
            binding.bottomNavigation.visibility = View.VISIBLE
            binding.bottomNavigation.selectedItemId = R.id.bottom_home
            binding.bottomAppBar.visibility = View.VISIBLE
            binding.backButton.visibility = View.GONE // Oculta el botón de retroceso personalizado
        }

        fragmentManager = supportFragmentManager
        cargarFragment(FragmentInicio())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_reservas -> {
                cargarFragment(FragmentReservas())
                binding.bottomNavigation.visibility = View.GONE
                binding.bottomAppBar.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
            }
            R.id.nav_nivel -> {
                cargarFragment(FragmentNivel())
                binding.bottomNavigation.visibility = View.GONE
                binding.bottomAppBar.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
            }
            R.id.nav_ranking -> {
                cargarFragment(FragmentRanking())
                binding.bottomNavigation.visibility = View.GONE
                binding.bottomAppBar.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
            }
            R.id.nav_campeonatos -> {
                cargarFragment(FragmentCampeonatos())
                binding.bottomNavigation.visibility = View.GONE
                binding.bottomAppBar.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
            }
            R.id.nav_eventos -> {
                cargarFragment(FragmentEventos())
                binding.bottomNavigation.visibility = View.GONE
                binding.bottomAppBar.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
            }
            R.id.nav_contacto -> {
                cargarFragment(FragmentContacto())
                binding.bottomNavigation.visibility = View.GONE
                binding.bottomAppBar.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
            }
            R.id.nav_logout -> {

                val firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.signOut()

                // Cerrar sesión en Google Sign-In
                googleSignInClient.signOut()


                // Redirigir al usuario a MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                cargarFragment(FragmentInicio())
                binding.bottomNavigation.visibility = View.VISIBLE
                binding.bottomAppBar.visibility = View.VISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)

        }else{

            super.onBackPressed()
        }

    }

    fun cargarFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

}