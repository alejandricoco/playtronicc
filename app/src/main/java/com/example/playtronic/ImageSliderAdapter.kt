package com.example.playtronic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playtronic.databinding.ActivityMenuBinding
import com.example.playtronic.fragments.fragmentsMenu.FragmentCampeonatos
import com.example.playtronic.fragments.fragmentsMenu.FragmentContacto
import com.example.playtronic.fragments.fragmentsMenu.FragmentEventos
import com.example.playtronic.fragments.fragmentsMenu.FragmentReservas

class ImageSliderAdapter(private val images: List<Int>,
                         private val fragments: List<Class<out Fragment>>,
                         private val fragmentManager: FragmentManager,
                         private val binding: ActivityMenuBinding) : RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)

        fun bind(image: Int) {
            imageView.setImageResource(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
        holder.imageView.setOnClickListener {
            val fragment = fragments[position].newInstance()
            cargarFragment(fragment)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    private fun cargarFragment(fragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()

        // Oculta la BottomNavigation y la AppBar, y muestra el botón de retroceso
        if (fragment is FragmentReservas || fragment is FragmentCampeonatos || fragment is FragmentEventos || fragment is FragmentContacto) {
            binding.bottomNavigation.visibility = View.GONE
            binding.bottomAppBar.visibility = View.GONE
            binding.backButton.visibility = View.VISIBLE
        } else {
            binding.bottomNavigation.visibility = View.VISIBLE
            binding.bottomAppBar.visibility = View.VISIBLE
            binding.backButton.visibility = View.GONE
        }
    }
}
