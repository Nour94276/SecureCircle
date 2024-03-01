package com.example.myapplication.presentation

// Assurez-vous d'importer correctement les composants Compose et autres nécessaires
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.example.myapplication.presentation.theme.MyApplicationTheme

// Classe Item correctement définie pour vos besoins
data class Item(val title: String, val description: String)

// Utilisation correcte de MenuItem pour votre menu
data class MenuItem(val title: String, val onClick: () -> Unit)

val itemsList = listOf(
    Item("Santé", "Description 1"),
    Item("Espace personnel", "Description 2"),
    Item("Géolocalisation", "Description 3"),
    Item("Urgence", "Description 4")
    // Ajoutez plus d'éléments selon le besoin
)

@Composable
fun MenuItemView(menuItem: Item ,  onClickAction: () -> Unit) {
    Text(
        text = menuItem.title,
        modifier = Modifier
            .padding(10.dp)
            .clickable(onClick = onClickAction)
        // La gestion du clic peut être ajoutée ici si nécessaire
    )
}

@Composable
fun MenuScreen() {
    MyApplicationTheme {
        ScalingLazyColumn {
            items(itemsList) { menuItem ->
                MenuItemView(menuItem = menuItem){
                    // L'action à exécuter lors du clic

                }
            }
        }
    }
}

@Composable
fun ItemView(item: Item) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = item.title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = item.description)
    }
}

@Composable
fun ItemList() {
    LazyColumn {
        items(itemsList) { item ->
            ItemView(item = item)
        }
    }
}
