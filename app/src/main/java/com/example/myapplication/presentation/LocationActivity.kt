package com.example.myapplication.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.location.GpsStatus
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class LocationActivity : AppCompatActivity(), MapListener, GpsStatus.Listener {

    private lateinit var mMap: MapView
    private lateinit var controller: IMapController
    private lateinit var mMyLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location) // Assurez-vous que le layout est correctement nommé.

        // Configuration de OSMdroid
        Configuration.getInstance().load(this, getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE))

        // Initialisation de la MapView
        mMap = findViewById(R.id.osmmap) // Assurez-vous que l'ID correspond dans votre layout XML.
        mMap.setMultiTouchControls(true) // Activation du zoom avec plusieurs doigts.

        // Configuration de l'overlay de localisation
        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true

        // Ajout de l'overlay à la MapView
        mMap.overlays.add(mMyLocationOverlay)

        // Configuration du contrôleur de la carte
        controller = mMap.controller
        controller.setZoom(18) // Exemple de niveau de zoom initial.

        // Ajout du listener pour la carte
        mMap.addMapListener(this)
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        event?.source?.mapCenter?.let {
            Log.e("TAG", "onScroll: Lat ${it.latitude} Lon ${it.longitude}")
        }
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        Log.e("TAG", "onZoom: Zoom level: ${event?.zoomLevel}")
        return false
    }

    override fun onGpsStatusChanged(event: Int) {
        // Implémentez la gestion des changements de statut GPS ici si nécessaire.
    }

    // Assurez-vous de demander les permissions nécessaires pour la localisation au démarrage de l'activité.
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
        mMyLocationOverlay.onResume() // Assurez-vous que l'overlay reprend correctement lorsque l'activité est relancée.
    }

    override fun onStop() {
        super.onStop()
        mMyLocationOverlay.onPause() // Assurez-vous que l'overlay est mis en pause correctement lorsque l'activité s'arrête.
    }
}
