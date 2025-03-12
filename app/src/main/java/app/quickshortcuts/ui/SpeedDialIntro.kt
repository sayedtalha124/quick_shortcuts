package app.quickshortcuts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.quickshortcuts.core.Screen
import java.time.format.TextStyle

@Composable
fun SpeedDialIntroScreen(navController: NavHostController) {
    var click = {
        navController.navigate(route = Screen.SpeedDial.route){
            popUpTo(Screen.SpeedDialIntro.route) {
                inclusive = true
            }
        }

    }


    Column(
        modifier = Modifier
            .systemBarsPadding()
            .padding(10.dp),   verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("This app allows you to add up to three contacts to your speed dial directly from the notifications",
            style =MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        HorizontalDivider(Modifier.padding(10.dp) )
        ElevatedButton(onClick = click) {
            Text("Continue")
        }


    }

}