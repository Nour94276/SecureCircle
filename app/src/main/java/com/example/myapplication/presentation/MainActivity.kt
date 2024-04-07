
package com.example.myapplication.presentation
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

class MainActivity : ComponentActivity() , SensorEventListener {
    private lateinit var socket: Socket
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private var stepCounterSensor: Sensor? = null
    private var heartRate by mutableStateOf("En attente...")
    private var steps by mutableStateOf("En attente...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            setupSensors()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BODY_SENSORS), 1)
        }
        setContent {
            WearApp {
                connectAndSendMessage()
            }
        }
    }

    private fun setupSensors() {
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        heartRateSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        stepCounterSensor?.also { steps ->
            sensorManager.registerListener(this, steps, SensorManager.SENSOR_DELAY_UI)
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
            Column(modifier = Modifier.fillMaxSize()) {
                Text(text = "Fréquence cardiaque: $heartRate")
                Text(text = "Nombre de pas: $steps")
            }
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
    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_HEART_RATE -> {
                heartRate = "Fréquence cardiaque: ${event.values.first()}"
            }
            Sensor.TYPE_STEP_COUNTER -> {
                steps = "Nombre de pas: ${event.values.first()}"
            }
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.  Unlike
     * onSensorChanged(), this is only called when this accuracy value changes.
     *
     *
     * See the SENSOR_STATUS_* constants in
     * [SensorManager][android.hardware.SensorManager] for details.
     *
     * @param accuracy The new accuracy of this sensor, one of
     * `SensorManager.SENSOR_STATUS_*`
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }

    private fun connectAndSendMessage() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            socket = IO.socket("http://10.0.2.2:3000") // Utilisez "http://localhost:3000" si testé sur un ordinateur
            socket.on(Socket.EVENT_CONNECT) {
                Log.d("SocketIO", "Connected to the server")
                getLastLocation { location ->
                    runOnUiThread {
                        val message = "Location: $location, Heart Rate: $heartRate, Steps: $steps"
                        socket.emit("messageFromWatch", message)
                        Log.d("SocketIO", "Message envoyé: $message")
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
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

}