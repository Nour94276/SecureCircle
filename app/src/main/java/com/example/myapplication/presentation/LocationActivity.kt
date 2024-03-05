package com.example.myapplication.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import world.mappable.mapkit.MapKitFactory
import world.mappable.mapkit.mapview.MapView

class LocationActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: MapView
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Assurez-vous que votre clé API est valide et active
        MapKitFactory.setApiKey("pk_cOgLSSEnQqltRLvAtFiMnUCMuMawrLVBGmGVPoYyRaGrRdevsLFlyCzlUkPCHpop")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_location)

        // Initialisation de FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialisation de MapView
        mapView = findViewById(R.id.mapView) // Assurez-vous que mapView correspond à l'ID de votre MapView dans activity_location.xml

        checkLocationPermission()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLastLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // Utilisez la localisation ici
                    Toast.makeText(this, "Latitude: ${location.latitude}, Longitude: ${location.longitude}", Toast.LENGTH_LONG).show()
                    // Mettez à jour la position sur la carte ici en utilisant les fonctions MapKit.
                    // Remarque : Vous devrez remplacer ce qui suit par les méthodes appropriées fournies par MapKit.
                    // mapView.setCenter(Point(location.latitude, location.longitude), animated: true)
                } else {
                    Toast.makeText(this, "Localisation non disponible", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            checkLocationPermission()
        }
    }
}
