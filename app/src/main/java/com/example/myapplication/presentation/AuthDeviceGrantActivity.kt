package com.example.myapplication.presentation

import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels

class AuthDeviceGrantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        val viewModel by viewModels<AuthDeviceGrantViewModel>()

        // Start the OAuth flow when the user presses the button
        findViewById<View>(R.id.authenticateButton).setOnClickListener {
            viewModel.startAuthFlow()
        }

        // Show current status on the screen
        viewModel.status.observe(this) { statusText ->
            findViewById<TextView>(R.id.status_text_view).text = resources.getText(statusText)
        }

        // Show dynamic content on the screen
        viewModel.result.observe(this) { resultText ->
            findViewById<TextView>(R.id.result_text_view).text = resultText
        }
    }
}