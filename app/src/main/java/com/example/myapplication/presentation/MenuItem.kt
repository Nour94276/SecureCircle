package com.example.myapplication.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.example.myapplication.presentation.theme.MyApplicationTheme

// Étape 2: Définir les éléments du menu
data class MenuItem(val title: String, val onClick: () -> Unit)

// Étape 3: Composable pour l'élément de menu
@Composable
fun MenuItemView(menuItem: MenuItem) {
    Text(
        text = menuItem.title,
        modifier = Modifier.padding(10.dp)
        // Ajoutez ici la gestion du clic si nécessaire
    )
}

// Étape 4: Afficher le menu avec ScalingLazyColumn
@Composable
fun MenuScreen(menuItems: List<MenuItem>) {
    MyApplicationTheme {
        ScalingLazyColumn {
            items(menuItems) { menuItem ->
                MenuItemView(menuItem = menuItem)
            }
        }
    }
}

// Étape 5: Gérer les actions de menu (dans la logique de navigation ou de clic)
