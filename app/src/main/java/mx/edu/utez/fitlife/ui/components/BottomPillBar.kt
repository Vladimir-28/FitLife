package mx.edu.utez.fitlife.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import mx.edu.utez.fitlife.ui.theme.PrimaryBlue
import androidx.compose.ui.graphics.Color

@Composable
fun BottomPillBar(nav: NavController, selected: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        NavButton("home", selected, nav, Icons.Default.Home)
        NavButton("profile", selected, nav, Icons.Default.Person)
    }
}

@Composable
private fun NavButton(
    route:String,
    selected:String,
    nav:NavController,
    icon:androidx.compose.ui.graphics.vector.ImageVector
){

    val active = route == selected

    Box(
        modifier = Modifier
            .background(
                if(active) PrimaryBlue else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
    ){

        IconButton(
            onClick = { nav.navigate(route) }
        ) {

            Icon(
                icon,
                null,
                tint = if(active) Color.White else Color.Gray
            )
        }
    }
}
