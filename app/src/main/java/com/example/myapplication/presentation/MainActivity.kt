/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.myapplication.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.myapplication.R
import com.example.myapplication.presentation.theme.MyApplicationTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            var showMenuScreen by remember { mutableStateOf(false) }

            if (!showMenuScreen) {
                // L'écran d'accueil avec le bouton pour aller au menu
                WearApp("Android") { showMenuScreen = true }
            } else {

                val context = LocalContext.current
                // L'écran de menu
                MenuScreen(context)
            }
        }
    }

    @Composable
    fun WearApp(greetingName: String, onMenuButtonClick: () -> Unit) {
        MyApplicationTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                TimeText()
                Greeting(greetingName = greetingName)
                Chip(
                    onClick = onMenuButtonClick,
                    label = { Text("Go to Menu") },
                    colors = ChipDefaults.primaryChipColors()
                )
            }
        }
    }

    @Composable
    fun Greeting(greetingName: String) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            text = stringResource(R.string.hello_world, greetingName)
        )
    }

    @Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
    @Composable
    fun DefaultPreview() {
    }
}