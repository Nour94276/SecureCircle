
package com.example.myapplication.presentation
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.myapplication.presentation.theme.MyApplicationTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.EngineIOException

class MainActivity : ComponentActivity() {
    private lateinit var socket: Socket
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp {
                connectAndSendMessage()
            }
        }
    }



    private fun getLastLocation(onLocationFetched: (String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // La permission n'a pas été accordée
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val locationString = "Latitude: ${it.latitude}, Longitude: ${it.longitude}"
                    onLocationFetched(locationString)
                } ?: run {
                    // Aucune localisation disponible, utilisez une localisation par défaut
                    onLocationFetched("Latitude: 48.8584, Longitude: 2.2945") // Localisation par défaut
                }
            }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission a été accordée
                connectAndSendMessage()
            } else {
                // La permission a été refusée
                Log.d("MainActivity", "Permission de localisation non accordée.")
            }
        }
    }

    @Composable
    fun WearApp(onButtonClick: () -> Unit) {
        MyApplicationTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                Chip(
                    onClick = onButtonClick,
                    label = { Text("Connect and Send Message") },
                    colors = ChipDefaults.primaryChipColors()
                )
            }
        }
    }
    private fun connectAndSendMessage() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            socket = IO.socket("http://10.0.2.2:3000") // Utilisez "http://localhost:3000" si testé sur un ordinateur
            socket.on(Socket.EVENT_CONNECT) {
                Log.d("SocketIO", "Connected to the server")
                getLastLocation { location ->
                    runOnUiThread {
                        socket.emit("messageFromWatch", "Hello from watch!")
                        socket.emit("messageForlocation", "Message and location sent: $location")
                        Log.d("SocketIO", "Message and location sent: $location")
                    }
                }
            }.on(Socket.EVENT_DISCONNECT) {
                Log.d("SocketIO", "Disconnected from the server")
            }.on(Socket.EVENT_CONNECT_ERROR) { args ->
                if (args[0] is EngineIOException) {
                    val e = args[0] as EngineIOException
                    Log.e("SocketIO", "Connection error: ${e.message}")
                    e.printStackTrace() // Prints the execution stack trace

                    // Pour imprimer des détails supplémentaires spécifiques à EngineIOException
                    Log.e("SocketIO", "Exception details: Cause -> ${e.cause}, LocalizedMessage -> ${e.localizedMessage}")
                } else {
                    Log.e("SocketIO", "Connection error: Unknown error ${args[0]}")
                }
            }
            socket.connect()

        } catch (e: Exception) {
            Log.e("SocketIO", "Exception: ${e.message}")
            e.printStackTrace()
        }
    }

}