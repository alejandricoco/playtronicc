package com.example.playtronic.fragments.fragmentsMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.playtronic.MenuActivity
import com.example.playtronic.R


class GlobalFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_global, container, false)
        val fotillo: ImageView = view.findViewById(R.id.fotillo)
        val titulillo: TextView = view.findViewById(R.id.titulillo)
        val fechilla: TextView = view.findViewById(R.id.fechilla)
        val cuerpillo: TextView = view.findViewById(R.id.cuerpillo)

        (activity as MenuActivity).binding.toolbar.visibility = View.VISIBLE
        (activity as MenuActivity).binding.backButton.visibility = View.VISIBLE
        (activity as MenuActivity).binding.bottomNavigation.visibility = View.GONE
        (activity as MenuActivity).binding.bottomAppBar.visibility = View.GONE

        val image = arguments?.getString("image")?: ""
        val title = arguments?.getString("title")?: ""
        val date = arguments?.getString("date")?: ""
        val cuerpo = arguments?.getString("cuerpo")?: ""

        Glide.with(this).load(image).into(fotillo)
        titulillo.text = title
        fechilla.text = date
        cuerpillo.text = cuerpo

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MenuActivity).binding.toolbar.visibility = View.VISIBLE
        (activity as MenuActivity).binding.backButton.visibility = View.VISIBLE
        (activity as MenuActivity).binding.bottomNavigation.visibility = View.GONE
        (activity as MenuActivity).binding.bottomAppBar.visibility = View.GONE
    }

}