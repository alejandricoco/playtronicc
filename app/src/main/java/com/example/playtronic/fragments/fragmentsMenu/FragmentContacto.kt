package com.example.playtronic.fragments.fragmentsMenu

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.playtronic.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration


class FragmentContacto : Fragment(){

    private lateinit var mapView: MapView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().load(context, context?.getSharedPreferences("prefs", 0))

        mapView = view.findViewById<MapView>(R.id.mapView)
        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(19.0)
        val startPoint = GeoPoint(37.625197, -0.993389)
        mapController.setCenter(startPoint)

        val startMarker = Marker(mapView)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.title = "Club de Tenis Cartagena"
        val chincheta: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.locationnn)
        startMarker.icon = chincheta
        mapView.overlays.add(startMarker)

        // CardViews
        val cardLlamada = view.findViewById<CardView>(R.id.card_llamada)
        val cardWhatsApp = view.findViewById<CardView>(R.id.card_whatsapp)
        val cardEmail = view.findViewById<CardView>(R.id.card_email)
        val cardUbicacion = view.findViewById<CardView>(R.id.card_ubicacion)

        // OnClickListeners
        cardLlamada.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:637752873")
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 1)
            } else {
                startActivity(intent)
            }
        }

        cardWhatsApp.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=+34637752873"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        cardEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:info@playtronic.com")
            startActivity(intent)
        }

        cardUbicacion.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:37.625197,-0.993389?q=Club de Tenis Cartagena")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }



    }





    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
    }


    private fun cargarFragment(fragment: Fragment) {
        // Asegúrate de que la actividad contenedora no sea nula
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.drawer_layout, fragment) // cargamos en el drawer del MenuActivity el fragmento que queremos
        fragmentTransaction.addToBackStack(null) // Permite volver al FragmentLogin al presionar el botón atrás
        fragmentTransaction.commit()
    }
}